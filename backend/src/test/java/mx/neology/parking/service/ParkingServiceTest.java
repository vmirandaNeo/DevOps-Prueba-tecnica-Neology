package mx.neology.parking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import mx.neology.parking.domain.Stay;
import mx.neology.parking.domain.Vehicle;
import mx.neology.parking.domain.VehicleType;
import mx.neology.parking.dto.MovementResponse;
import mx.neology.parking.dto.ResidentPaymentResponse;
import mx.neology.parking.error.ApiException;
import mx.neology.parking.repository.StayRepository;
import mx.neology.parking.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private StayRepository stayRepository;

    private MutableClock clock;
    private ParkingService service;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-15T10:00:00Z"));
        service = new ParkingService(vehicleRepository, stayRepository, clock);
    }

    @Test
    void chargesNonResidentAtFiftyCentsPerStartedMinute() {
        Vehicle vehicle = new Vehicle("NR-001", VehicleType.NO_RESIDENTE);
        Stay stay = new Stay(vehicle, LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
        when(vehicleRepository.findById("NR-001")).thenReturn(Optional.of(vehicle));
        when(stayRepository.findFirstByVehiclePlateAndExitTimeIsNullOrderByEntryTimeDesc("NR-001"))
                .thenReturn(Optional.of(stay));

        clock.advanceSeconds(601);
        MovementResponse response = service.registerExit("nr-001");

        assertThat(response.minutos()).isEqualTo(11);
        assertThat(response.importe()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(stay.isOpen()).isFalse();
    }

    @Test
    void accumulatesResidentMinutesForMonthlyReport() {
        Vehicle vehicle = new Vehicle("RES-001", VehicleType.RESIDENTE);
        Stay stay = new Stay(vehicle, LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
        when(vehicleRepository.findById("RES-001")).thenReturn(Optional.of(vehicle));
        when(stayRepository.findFirstByVehiclePlateAndExitTimeIsNullOrderByEntryTimeDesc("RES-001"))
                .thenReturn(Optional.of(stay));
        when(vehicleRepository.findAllByTypeOrderByPlateAsc(VehicleType.RESIDENTE))
                .thenReturn(List.of(vehicle));

        clock.advanceSeconds(600);
        service.registerExit("RES-001");
        List<ResidentPaymentResponse> report = service.residentPayments();

        assertThat(report).singleElement().satisfies(item -> {
            assertThat(item.minutosAcumulados()).isEqualTo(10);
            assertThat(item.importe()).isEqualByComparingTo(new BigDecimal("0.50"));
        });
    }

    @Test
    void rejectsASecondOpenStay() {
        Vehicle vehicle = new Vehicle("ABC-123", VehicleType.OFICIAL);
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(vehicle));
        when(stayRepository.existsByVehiclePlateAndExitTimeIsNull("ABC-123")).thenReturn(true);

        assertThatThrownBy(() -> service.registerEntry("abc-123"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("estancia abierta");
    }

    @Test
    void resetsClosedStaysAndResidentCounters() {
        Vehicle resident = new Vehicle("RES-002", VehicleType.RESIDENTE);
        resident.addAccumulatedMinutes(30);
        when(stayRepository.findAllByExitTimeIsNull()).thenReturn(List.of());
        when(vehicleRepository.findAll()).thenReturn(List.of(resident));

        service.startNewMonth();

        verify(stayRepository).deleteAllInBatch();
        assertThat(resident.getAccumulatedMinutes()).isZero();
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}

