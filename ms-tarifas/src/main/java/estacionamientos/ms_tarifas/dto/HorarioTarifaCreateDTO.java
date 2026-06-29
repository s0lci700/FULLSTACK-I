package estacionamientos.ms_tarifas.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para crear un horario de tarifa")
public class HorarioTarifaCreateDTO {

    @NotNull(message = "El id de tarifa es obligatorio")
    private Long idTarifa;

    @NotBlank(message = "El dia tipo es obligatorio")
    private String diaTipo;

    @NotNull(message = "La hora inicio es obligatoria")
    private LocalDateTime horaInicio;

    @NotNull(message = "La hora fin es obligatoria")
    private LocalDateTime horaFin;

    @NotNull(message = "El multiplicador es obligatorio")
    private Double multiplicador;
}
