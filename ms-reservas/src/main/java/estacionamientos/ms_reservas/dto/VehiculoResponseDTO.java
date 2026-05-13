package estacionamientos.ms_reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class VehiculoResponseDTO {

    private Long id;
    private Long idClienteRef;
    private Boolean activo;

}
