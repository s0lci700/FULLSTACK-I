package estacionamientos.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteSuscripcionCreateDTO {

    @NotNull(message = "El id de suscripcion es obligatorio")
    private Long idSuscripcion;

    @NotNull(message = "La fecha inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;
}