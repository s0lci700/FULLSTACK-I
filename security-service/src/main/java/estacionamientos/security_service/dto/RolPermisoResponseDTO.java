package estacionamientos.security_service.dto;

import estacionamientos.security_service.model.Permiso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolPermisoResponseDTO {

    // ID de la asignacion rol-permiso
    private Long id;

    // ID del rol al que se le asigno el permiso
    private Long idRol;

    // Nombre del permiso asignado
    private Permiso permiso;

}