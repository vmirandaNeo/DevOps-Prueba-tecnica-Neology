package mx.neology.parking.dto;

import java.math.BigDecimal;

public record ResidentPaymentResponse(
        String placa,
        long minutosAcumulados,
        BigDecimal importe
) {
}

