package mx.neology.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VehicleRequest(
        @NotBlank(message = "La placa es obligatoria")
        @Size(min = 3, max = 12, message = "La placa debe tener entre 3 y 12 caracteres")
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "La placa solo acepta letras, números y guiones")
        String placa
) {
}

