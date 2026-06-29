package estacionamientos.ms_vehiculos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un vehículo existente
 * La patente NO se puede cambiar (inmutable)
 */
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos para actualizar un vehículo (la patente es inmutable)")
public class VehiculoUpdateDTO {

    @NotBlank(message = "La marca es obligatoria")
    @Schema(
        description = "Marca del vehículo",
        example = "Toyota",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Schema(
        description = "Modelo del vehículo",
        example = "Corolla",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String modelo;

    @Schema(
        description = "Color del vehículo",
        example = "Blanco"
    )
    private String color;

    @Schema(
        description = "Año de fabricación",
        example = "2021"
    )
    private Integer anio;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    @Schema(
        description = "ID del tipo de vehículo actualizado",
        example = "2",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long idTipoVehiculo;

    @NotNull(message = "El estado activo es obligatorio")
    @Schema(
        description = "Estado del vehículo: true = activo, false = desactivado",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean activo;
}
