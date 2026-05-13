package estacionamientos.ms_reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ClienteResponseDTO {

    private Long id;
    private Boolean activo;
}
