package estacionamientos.security_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para asignar un permiso a un rol")
@Data @NoArgsConstructor @AllArgsConstructor
public class RolPermisoCreateDTO {

    @Schema(description = "ID del rol", example = "1")
    @NotNull
    private Long idRol;

    @Schema(description = "ID del permiso a asignar", example = "2")
    @NotNull
    private Long idPermiso;
}