package estacionamientos.ms_reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class EspacioResponseDTO {
    private Long id;
    private Boolean disponible;
    private Boolean activo;
}
