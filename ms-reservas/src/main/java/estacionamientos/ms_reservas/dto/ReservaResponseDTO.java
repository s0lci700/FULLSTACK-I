package estacionamientos.ms_reservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import estacionamientos.ms_reservas.model.EstadoEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReservaResponseDTO {

    @Schema(description = "ID de la reserva", example = "1")
    private Long id;
    @Schema(description = "ID del cliente", example = "1")
    private Long idCliente;
    @Schema(description = "ID del vehículo", example = "1")
    private Long idVehiculo;
    @Schema(description = "ID del espacio reservado", example = "1")
    private Long idEspacio;
    @Schema(description = "Fecha y hora de inicio de la reserva", example = "2026-07-01T10:00:00")
    private LocalDateTime fechaInicio;
    @Schema(description = "Fecha y hora de fin de la reserva", example = "2026-07-01T12:00:00")
    private LocalDateTime fechaFin;
    @Schema(description = "Estado actual de la reserva", example = "PENDIENTE")
    private EstadoEnums estado;
    @Schema(description = "Fecha y hora de creación de la reserva", example = "2026-06-29T09:00:00")
    private LocalDateTime fechaCreacion;
}
