package estacionamientos.ms_espacios.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para actualizar un tipo de espacio")
public class TipoEspacioUpdateDTO {

    @Schema(description = "Nombre descriptivo del tipo", example = "Estándar")
    @NotBlank
    private String nombre;

    @Schema(description = "Descripción adicional", example = "Espacio para autos particulares")
    private String descripcion;

    @Schema(description = "Factor multiplicador de precio para este tipo", example = "1.0")
    @NotNull
    private BigDecimal factorPrecio;
}
