package mx.neology.parking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import mx.neology.parking.domain.VehicleType;

public record MovementResponse(
        String placa,
        VehicleType tipo,
        LocalDateTime fechaHora,
        Long minutos,
        BigDecimal importe,
        String mensaje
) {
}

