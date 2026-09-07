package mx.neology.parking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import mx.neology.parking.domain.Stay;
import mx.neology.parking.domain.Vehicle;
import mx.neology.parking.domain.VehicleType;
import mx.neology.parking.dto.MessageResponse;
import mx.neology.parking.dto.MovementResponse;
import mx.neology.parking.dto.ResidentPaymentResponse;
import mx.neology.parking.dto.StayResponse;
import mx.neology.parking.dto.VehicleDetailResponse;
import mx.neology.parking.dto.VehicleResponse;
import mx.neology.parking.error.ApiException;
import mx.neology.parking.repository.StayRepository;
import mx.neology.parking.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParkingService {

    static final BigDecimal RESIDENT_RATE = new BigDecimal("0.05");
    static final BigDecimal NON_RESIDENT_RATE = new BigDecimal("0.50");

    private final VehicleRepository vehicleRepository;
    private final StayRepository stayRepository;
    private final Clock clock;

    public ParkingService(VehicleRepository vehicleRepository, StayRepository stayRepository, Clock clock) {
        this.vehicleRepository = vehicleRepository;
        this.stayRepository = stayRepository;
        this.clock = clock;
    }

    @Transactional
    public VehicleResponse createVehicle(String rawPlate, VehicleType type) {
        String plate = normalizePlate(rawPlate);
        if (vehicleRepository.existsById(plate)) {
            throw new ApiException(HttpStatus.CONFLICT, "La placa " + plate + " ya está registrada");
        }
        Vehicle vehicle = vehicleRepository.save(new Vehicle(plate, type));
        return toVehicleResponse(vehicle, false);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> listVehicles() {
        Set<String> openPlates = new HashSet<>();
        stayRepository.findAllByExitTimeIsNull()
                .forEach(stay -> openPlates.add(stay.getVehicle().getPlate()));

        return vehicleRepository.findAllByOrderByPlateAsc().stream()
                .map(vehicle -> toVehicleResponse(vehicle, openPlates.contains(vehicle.getPlate())))
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleDetailResponse getVehicle(String rawPlate) {
        String plate = normalizePlate(rawPlate);
        Vehicle vehicle = findVehicle(plate);
        List<Stay> stays = stayRepository.findByVehiclePlateOrderByEntryTimeDesc(plate);
        boolean open = stays.stream().anyMatch(Stay::isOpen);
        List<StayResponse> stayResponses = stays.stream().map(this::toStayResponse).toList();
        return new VehicleDetailResponse(toVehicleResponse(vehicle, open), stayResponses);
    }

    @Transactional
    public MovementResponse registerEntry(String rawPlate) {
        String plate = normalizePlate(rawPlate);
        Vehicle vehicle = findVehicle(plate);
        if (stayRepository.existsByVehiclePlateAndExitTimeIsNull(plate)) {
            throw new ApiException(HttpStatus.CONFLICT, "El vehículo ya tiene una estancia abierta");
        }

        LocalDateTime now = nowUtc();
        stayRepository.save(new Stay(vehicle, now));
        return new MovementResponse(plate, vehicle.getType(), now, null, null,
                "Entrada registrada correctamente");
    }

    @Transactional
    public MovementResponse registerExit(String rawPlate) {
        String plate = normalizePlate(rawPlate);
        Vehicle vehicle = findVehicle(plate);
        Stay stay = stayRepository
                .findFirstByVehiclePlateAndExitTimeIsNullOrderByEntryTimeDesc(plate)
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT,
                        "El vehículo no tiene una estancia abierta"));

        LocalDateTime now = nowUtc();
        long minutes = billableMinutes(stay.getEntryTime(), now);
        BigDecimal amount = calculateAmount(vehicle.getType(), minutes);

        stay.close(now, minutes, amount);
        if (vehicle.getType() == VehicleType.RESIDENTE) {
            vehicle.addAccumulatedMinutes(minutes);
        }

        return new MovementResponse(plate, vehicle.getType(), now, minutes, amount,
                "Salida registrada correctamente");
    }

    @Transactional(readOnly = true)
    public List<ResidentPaymentResponse> residentPayments() {
        return vehicleRepository.findAllByTypeOrderByPlateAsc(VehicleType.RESIDENTE).stream()
                .map(vehicle -> new ResidentPaymentResponse(
                        vehicle.getPlate(),
                        vehicle.getAccumulatedMinutes(),
                        residentAmount(vehicle.getAccumulatedMinutes())
                ))
                .toList();
    }

    @Transactional
    public MessageResponse startNewMonth() {
        List<Stay> openStays = stayRepository.findAllByExitTimeIsNull();
        if (!openStays.isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "No se puede iniciar el mes mientras existan estancias abiertas");
        }

        stayRepository.deleteAllInBatch();
        vehicleRepository.findAll().forEach(Vehicle::resetAccumulatedMinutes);
        return new MessageResponse("Nuevo mes iniciado; estancias y acumulados fueron reiniciados");
    }

    private Vehicle findVehicle(String plate) {
        return vehicleRepository.findById(plate)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "No existe un vehículo con la placa " + plate));
    }

    private String normalizePlate(String plate) {
        if (plate == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La placa es obligatoria");
        }
        String normalized = plate.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("^[A-Z0-9-]{3,12}$")) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "La placa debe tener entre 3 y 12 caracteres alfanuméricos o guiones");
        }
        return normalized;
    }

    private LocalDateTime nowUtc() {
        return LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    }

    private long billableMinutes(LocalDateTime entry, LocalDateTime exit) {
        long seconds = Math.max(0, Duration.between(entry, exit).getSeconds());
        return Math.max(1, (seconds + 59) / 60);
    }

    private BigDecimal calculateAmount(VehicleType type, long minutes) {
        return switch (type) {
            case OFICIAL -> BigDecimal.ZERO.setScale(2);
            case RESIDENTE -> RESIDENT_RATE.multiply(BigDecimal.valueOf(minutes)).setScale(2,
                    RoundingMode.HALF_UP);
            case NO_RESIDENTE -> NON_RESIDENT_RATE.multiply(BigDecimal.valueOf(minutes)).setScale(2,
                    RoundingMode.HALF_UP);
        };
    }

    private BigDecimal residentAmount(long accumulatedMinutes) {
        return RESIDENT_RATE.multiply(BigDecimal.valueOf(accumulatedMinutes))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private VehicleResponse toVehicleResponse(Vehicle vehicle, boolean open) {
        return new VehicleResponse(
                vehicle.getPlate(),
                vehicle.getType(),
                vehicle.getAccumulatedMinutes(),
                residentAmount(vehicle.getAccumulatedMinutes()),
                open
        );
    }

    private StayResponse toStayResponse(Stay stay) {
        return new StayResponse(
                stay.getId(),
                stay.getEntryTime(),
                stay.getExitTime(),
                stay.getDurationMinutes(),
                stay.getAmount(),
                stay.isOpen()
        );
    }
}

