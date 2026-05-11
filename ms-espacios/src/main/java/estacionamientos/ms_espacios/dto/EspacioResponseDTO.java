package estacionamientos.ms_espacios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspacioResponseDTO {
    private Long id;
    private String numero;
    private String zona;
    private Integer piso;
    private TipoEspacioResponseDTO tipoEspacio;
    private Boolean disponible;
    private Boolean activo;
}