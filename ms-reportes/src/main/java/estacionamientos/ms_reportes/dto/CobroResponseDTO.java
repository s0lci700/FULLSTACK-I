package estacionamientos.ms_reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class CobroResponseDTO {
    @Schema(description = "ID del cobro", example = "1")
    private Long id;
    @Schema(description = "ID del acceso asociado", example = "1")
    private Long idAcceso;
    @Schema(description = "ID del cliente", example = "1")
    private Long idCliente;
    @Schema(description = "Minutos de permanencia", example = "120")
    private Long minutos;
    @Schema(description = "Monto base calculado", example = "5000.0")
    private Double montoBase;
    @Schema(description = "Monto final después de descuentos", example = "4500.0")
    private Double montoFinal;
    @Schema(description = "Fecha y hora del cobro", example = "2026-07-01T12:05:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCobro;
    @Schema(description = "Nombre del método de pago utilizado", example = "Débito Banco Chile")
    private String metodoPago;
}
