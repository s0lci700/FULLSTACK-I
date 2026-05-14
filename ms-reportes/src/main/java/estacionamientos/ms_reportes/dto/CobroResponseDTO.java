package estacionamientos.ms_reportes.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class CobroResponseDTO {
    private Long id;
    private Long idAcceso;
    private Long idCliente;
    private Long minutos;
    private Double montoBase;
    private Double montoFinal;
    private LocalDateTime fechaHoraCobro;
    private String metodoPago;

}
