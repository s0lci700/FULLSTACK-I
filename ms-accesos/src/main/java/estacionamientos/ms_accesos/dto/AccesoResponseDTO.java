package estacionamientos.ms_accesos.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AccesoResponseDTO {

    private Long id;

    @Schema(description = "ID del vehículo que ingresa", example = "1")
    private Long idVehiculo;

    private Long idEspacio;

    @Schema(description = "ID de la reserva asociada", example = "1")
    private Long idReserva;

    @Schema(description = "Patente escaneada en la barrera", example = "ABCD12")
    private String patenteEscaneada;

    @Schema(description = "Fecha y hora de entrada", example = "2026-06-29T12:30:00")
    private LocalDateTime fechaHoraEntrada;

    private LocalDateTime fechaHoraSalida;

    private Long minutos;

    private String estado;
}
