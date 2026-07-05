package estacionamientos.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolResponseDTO {

    @Schema(description = "Identificador del rol", example = "1")
    private Long id;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombre;

    @Schema(description = "Descripción del rol", example = "Administrador con acceso total al sistema")
    private String descripcion;
}
