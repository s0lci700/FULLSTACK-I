package estacionamientos.ms_reservas.dto;


import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para crear una reserva (queda en estado PENDIENTE)")
@Data @NoArgsConstructor @AllArgsConstructor
public class ReservaCreateDTO {

    @Schema(description = "FK lógica al cliente (db_usuarios)", example = "1")
    @NotNull
    private Long idCliente;

    @Schema(description = "FK lógica al vehículo (db_vehiculos)", example = "1")
    @NotNull
    private Long idVehiculo;

    @Schema(description = "FK lógica al espacio (db_espacios)", example = "1")
    @NotNull
    private Long idEspacio;

    @Schema(description = "Inicio de la reserva", example = "2026-06-15T10:00:00")
    @NotNull
    private LocalDateTime fechaInicio;

    @Schema(description = "Fin de la reserva", example = "2026-06-15T12:00:00")
    @NotNull
    private LocalDateTime fechaFin;
}
