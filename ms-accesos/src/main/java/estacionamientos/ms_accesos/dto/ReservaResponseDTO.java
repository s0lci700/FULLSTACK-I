package estacionamientos.ms_accesos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReservaResponseDTO {
// ReservaResponseDTO de ReservaCliente
    private Long id;
    private Long idEspacio;
    private Long idVehiculo;
    private String estado;
}
