package estacionamientos.ms_tarifas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioTarifaCreateDTO {

    @NotNull(message = "El id de tarifa es obligatorio")
    private Long idTarifa;

    @NotBlank(message = "El dia tipo es obligatorio")
    private String diaTipo;

    @NotBlank(message = "La hora inicio es obligatoria")
    private String horaInicio;

    @NotBlank(message = "La hora fin es obligatoria")
    private String horaFin;

    @NotNull(message = "El multiplicador es obligatorio")
    private Double multiplicador;
}
