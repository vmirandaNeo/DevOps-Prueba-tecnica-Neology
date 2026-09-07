package mx.neology.parking.dto;

import java.util.List;

public record VehicleDetailResponse(
        VehicleResponse vehiculo,
        List<StayResponse> estancias
) {
}

