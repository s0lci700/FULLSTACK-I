package estacionamientos.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuscripcionResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double descuentoPorcentaje;
    private Boolean activo;
}