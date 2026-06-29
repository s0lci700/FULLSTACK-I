package estacionamientos.ms_reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OcupacionReporteDTO {
    @Schema(description = "Total de espacios en el sistema", example = "20")
    private int totalEspacios;
    @Schema(description = "Espacios disponibles actualmente", example = "8")
    private int disponibles;
    @Schema(description = "Espacios ocupados actualmente", example = "12")
    private int ocupados;
}
