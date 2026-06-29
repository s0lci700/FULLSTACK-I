package estacionamientos.ms_espacios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspacioUpdateDTO {

    @Schema(description = "Número del espacio", example = "A-01")
    @NotBlank(message = "El numero es obligatorio")
    private String numero;

    @Schema(description = "Zona del espacio", example = "A")
    private String zona;

    @Schema(description = "Piso del espacio", example = "1")
    @NotNull(message = "El piso es obligatorio")
    private Integer piso;

    @Schema(description = "ID del tipo de espacio", example = "1")
    @NotNull(message = "El id del tipo de espacio es obligatorio")
    private Long idTipoEspacio;

    @Schema(description = "Indica si el espacio está activo", example = "true")
    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}
