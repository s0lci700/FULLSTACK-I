package estacionamientos.security_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import estacionamientos.security_service.model.Permiso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolPermisoResponseDTO {

    // ID de la asignacion rol-permiso
    @Schema(description = "ID de la asignación rol-permiso", example = "1")
    private Long id;

    // ID del rol al que se le asigno el permiso
    @Schema(description = "ID del rol al que se le asignó el permiso", example = "2")
    private Long idRol;

    // Nombre del permiso asignado
    @Schema(description = "Permiso asignado al rol")
    private Permiso permiso;

}