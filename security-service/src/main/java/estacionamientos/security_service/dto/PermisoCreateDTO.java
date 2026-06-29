package estacionamientos.security_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoCreateDTO {

    @Schema(description = "Nombre del permiso", example = "VER_VEHICULOS")
    @NotBlank
    private String nombre;

    @Schema(description = "Recurso al que aplica el permiso", example = "vehiculos")
    @NotBlank
    private String recurso;

    @Schema(description = "Acción permitida sobre el recurso", example = "GET")
    @NotBlank
    private String accion;
}
