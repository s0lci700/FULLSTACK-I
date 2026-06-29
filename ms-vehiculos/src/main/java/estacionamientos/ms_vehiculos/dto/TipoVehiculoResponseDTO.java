package estacionamientos.ms_vehiculos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoVehiculoResponseDTO {

    @Schema(description = "Nombre del tipo de vehículo", example = "Automóvil")
    @NotBlank
    private String nombre;

    @Schema(description = "Descripción del tipo de vehículo", example = "Vehículo de pasajeros estándar")
    private String descripcion;

    @Schema(description = "Factor de precio aplicado al tipo de vehículo", example = "1.0")
    @NotNull
    @Positive
    private BigDecimal factorPrecio;
}
