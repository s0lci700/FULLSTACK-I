package estacionamientos.security_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoResponseDTO {

    @Schema(description = "ID del permiso", example = "1")
    private Long id;

    @Schema(description = "Nombre del permiso", example = "VER_VEHICULOS")
    private String nombre;

    @Schema(description = "Recurso al que aplica el permiso", example = "vehiculos")
    private String recurso;

    @Schema(description = "Acción permitida sobre el recurso", example = "GET")
    private String accion;
}
