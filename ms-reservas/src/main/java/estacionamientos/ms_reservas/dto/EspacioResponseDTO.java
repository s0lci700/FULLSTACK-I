package estacionamientos.ms_reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class EspacioResponseDTO {
    private Long id;
    private String numero;
    private String zona;
    private Integer piso;
    private Long idTipoEspacio;
    private Boolean disponible;
    private Boolean activo;
}
