package estacionamientos.ms_espacios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspacioResponseDTO {
    @Schema(description = "ID del espacio", example = "1")
    private Long id;
    @Schema(description = "Número del espacio", example = "A-01")
    private String numero;
    @Schema(description = "Zona del espacio", example = "A")
    private String zona;
    @Schema(description = "Piso del espacio", example = "1")
    private Integer piso;
    @Schema(description = "Tipo de espacio asociado")
    private TipoEspacioResponseDTO tipoEspacio;
    @Schema(description = "Indica si el espacio está disponible", example = "true")
    private Boolean disponible;
    @Schema(description = "Indica si el espacio está activo", example = "true")
    private Boolean activo;
}
