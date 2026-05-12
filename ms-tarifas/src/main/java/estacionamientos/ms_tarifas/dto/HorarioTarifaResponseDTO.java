package estacionamientos.ms_tarifas.dto;

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
    private String horaInicio;
    private String horaFin;
    private Double multiplicador;
}