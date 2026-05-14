package estacionamientos.ms_reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponseDTO {
    private Long id;
    private String marca;
    private String modelo;
    private String color;
    private String patente;
    private Integer anio;
    private Long idTipoVehiculo;
    private Long idClienteRef;
    private Boolean activo;
}
