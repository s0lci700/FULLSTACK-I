package estacionamientos.ms_tarifas.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioTarifaResponseDTO {
    private Long id;
    private TarifaResponseDTO tarifa;
    private String diaTipo;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private Double multiplicador;
}