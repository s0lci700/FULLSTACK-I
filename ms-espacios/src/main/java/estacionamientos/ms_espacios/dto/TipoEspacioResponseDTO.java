package estacionamientos.ms_espacios.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta con los datos de un tipo de espacio
 * Consumido como parte de EspacioResponseDTO
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Schema(description = "Datos de un tipo de espacio de estacionamiento")
public class TipoEspacioResponseDTO {

    @Schema(
        description = "ID único del tipo de espacio",
        example = "1"
    )
    private Long id;

    @Schema(
        description = "Nombre descriptivo del tipo",
        example = "Estándar"
    )
    private String nombre;

    @Schema(
        description = "Descripción adicional",
        example = "Espacio para autos particulares"
    )
    private String descripcion;

    @Schema(
        description = "Factor multiplicador de precio para este tipo",
        example = "1.0"
    )
    private BigDecimal factorPrecio;
}