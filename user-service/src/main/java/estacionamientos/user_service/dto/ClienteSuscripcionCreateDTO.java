package estacionamientos.user_service.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteSuscripcionCreateDTO {

    @Schema(description = "ID de la suscripción", example = "1")
    @NotNull(message = "El id de suscripcion es obligatorio")
    private Long idSuscripcion;

    @Schema(description = "Fecha de inicio de la suscripción", example = "2026-07-01")
    @NotNull(message = "La fecha inicio es obligatoria")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de finalización de la suscripción (opcional)", example = "2026-07-31")
    private LocalDate fechaFin;
}
