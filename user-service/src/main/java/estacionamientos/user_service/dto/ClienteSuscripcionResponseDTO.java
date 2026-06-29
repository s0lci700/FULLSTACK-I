package estacionamientos.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Schema(description = "Suscripción activa asignada a un cliente")
@Data @AllArgsConstructor @NoArgsConstructor
public class ClienteSuscripcionResponseDTO {
    @Schema(description = "ID de la asignación", example = "1")
    private Long id;
    @Schema(description = "Cliente al que pertenece la suscripción")
    private ClienteResponseDTO cliente;
    @Schema(description = "Plan de suscripción asignado")
    private SuscripcionResponseDTO suscripcion;
    @Schema(description = "Fecha de inicio", example = "2026-07-01")
    private LocalDate fechaInicio;
    @Schema(description = "Fecha de vencimiento", example = "2026-07-31")
    private LocalDate fechaFin;
    @Schema(description = "Indica si la suscripción está vigente", example = "true")
    private Boolean activo;
}