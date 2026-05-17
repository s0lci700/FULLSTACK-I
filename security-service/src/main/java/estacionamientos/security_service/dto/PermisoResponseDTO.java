package estacionamientos.security_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoResponseDTO {

    private Long id;
    private String nombre;
    private String recurso;
    private String accion;
}