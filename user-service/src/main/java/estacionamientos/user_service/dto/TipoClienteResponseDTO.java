package estacionamientos.user_service.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoClienteResponseDTO {
    @Schema(description = "ID del tipo de cliente", example = "1")
    private Long id;
    @Schema(description = "Nombre del tipo de cliente", example = "Corporativo")
    private String nombre;
// private String descripcion;
    @Schema(description = "Porcentaje de descuento aplicado a este tipo de cliente", example = "5.0")
    private BigDecimal DescuentoPct;
}