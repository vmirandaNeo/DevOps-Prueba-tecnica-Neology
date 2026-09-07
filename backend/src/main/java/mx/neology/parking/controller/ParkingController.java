package mx.neology.parking.controller;

import jakarta.validation.Valid;
import java.util.List;
import mx.neology.parking.domain.VehicleType;
import mx.neology.parking.dto.MessageResponse;
import mx.neology.parking.dto.MovementResponse;
import mx.neology.parking.dto.ResidentPaymentResponse;
import mx.neology.parking.dto.VehicleDetailResponse;
import mx.neology.parking.dto.VehicleRequest;
import mx.neology.parking.dto.VehicleResponse;
import mx.neology.parking.service.ParkingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/vehiculos")
    List<VehicleResponse> listVehicles() {
        return parkingService.listVehicles();
    }

    @GetMapping("/vehiculos/{placa}")
    VehicleDetailResponse getVehicle(@PathVariable String placa) {
        return parkingService.getVehicle(placa);
    }

    @PostMapping("/vehiculos/oficiales")
    ResponseEntity<VehicleResponse> createOfficial(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingService.createVehicle(request.placa(), VehicleType.OFICIAL));
    }

    @PostMapping("/vehiculos/residentes")
    ResponseEntity<VehicleResponse> createResident(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingService.createVehicle(request.placa(), VehicleType.RESIDENTE));
    }

    @PostMapping("/vehiculos/no-residentes")
    ResponseEntity<VehicleResponse> createNonResident(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingService.createVehicle(request.placa(), VehicleType.NO_RESIDENTE));
    }

    @PostMapping("/estancias/entrada")
    ResponseEntity<MovementResponse> registerEntry(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingService.registerEntry(request.placa()));
    }

    @PostMapping("/estancias/salida")
    MovementResponse registerExit(@Valid @RequestBody VehicleRequest request) {
        return parkingService.registerExit(request.placa());
    }

    @GetMapping("/residentes/pagos")
    List<ResidentPaymentResponse> residentPayments() {
        return parkingService.residentPayments();
    }

    @PostMapping("/mes/iniciar")
    MessageResponse startNewMonth() {
        return parkingService.startNewMonth();
    }
}

