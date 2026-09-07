package mx.neology.parking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StayResponse(
        Long id,
        LocalDateTime fechaHoraEntrada,
        LocalDateTime fechaHoraSalida,
        Long minutos,
        BigDecimal importe,
        boolean abierta
) {
}

