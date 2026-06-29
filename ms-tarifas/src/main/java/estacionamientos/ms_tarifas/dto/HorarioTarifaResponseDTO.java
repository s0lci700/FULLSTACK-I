package estacionamientos.ms_tarifas.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta para un horario de tarifa")
public class HorarioTarifaResponseDTO {
    private Long id;
    private TarifaResponseDTO tarifa;
    private String diaTipo;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private Double multiplicador;
}
