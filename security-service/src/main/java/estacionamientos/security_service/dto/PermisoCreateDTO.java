package estacionamientos.security_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoCreateDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String recurso;

    @NotBlank
    private String accion;
}