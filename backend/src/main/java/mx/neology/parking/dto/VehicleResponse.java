package mx.neology.parking.dto;

import java.math.BigDecimal;
import mx.neology.parking.domain.VehicleType;

public record VehicleResponse(
        String placa,
        VehicleType tipo,
        long minutosAcumulados,
        BigDecimal importeResidente,
        boolean estanciaAbierta
) {
}

