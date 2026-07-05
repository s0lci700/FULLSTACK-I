package estacionamientos.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolUpdateDTO {

    @Schema(description = "Nombre del rol", example = "ADMIN")
    @NotBlank
    private String nombre;

    @Schema(description = "Descripción del rol", example = "Administrador con acceso total al sistema")
    private String descripcion;
}
