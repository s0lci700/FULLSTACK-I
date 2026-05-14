package estacionamientos.ms_reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OcupacionReporteDTO {
    private int totalEspacios;
    private int disponibles;
    private int ocupados;

}
