package estacionamientos.ms_espacios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para registrar un nuevo espacio de estacionamiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspacioCreateDTO {

    @Schema(description = "Número único del espacio", example = "A-01")
    @NotBlank(message = "El numero es obligatorio")
    private String numero;

    @Schema(description = "Zona del estacionamiento", example = "Norte")
    private String zona;

    @Schema(description = "Piso donde se ubica el espacio", example = "1")
    @NotNull(message = "El piso es obligatorio")
    private Integer piso;

    @Schema(description = "ID del tipo de espacio (tabla tipo_espacio)", example = "1")
    @NotNull(message = "El id del tipo de espacio es obligatorio")
    private Long idTipoEspacio;

    @Schema(description = "Si el espacio está libre para ser usado", example = "true")
    @NotNull(message = "La disponibilidad es obligatoria")
    private Boolean disponible;

    @Schema(description = "Borrado lógico: false = desactivado", example = "true")
    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}