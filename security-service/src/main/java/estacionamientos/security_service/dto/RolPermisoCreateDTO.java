package estacionamientos.security_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolPermisoCreateDTO {

    // ID del rol que viene de auth-service (FK logica)
    // No guardamos el objeto Rol aqui, solo su ID
    @NotNull
    private Long idRol;

    // ID del permiso que queremos asignar a ese rol
    @NotNull
    private Long idPermiso;
}