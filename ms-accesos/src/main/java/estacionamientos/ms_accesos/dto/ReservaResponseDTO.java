package estacionamientos.ms_accesos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta con datos mínimos de la reserva
 * Consumido como DTO Feign desde ms-reservas
 */
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos básicos de una reserva consultados desde ms-reservas")
public class ReservaResponseDTO {

    @Schema(
        description = "ID único de la reserva",
        example = "5"
    )
    private Long id;

    @Schema(
        description = "ID del espacio de estacionamiento reservado",
        example = "7"
    )
    private Long idEspacio;

    @Schema(
        description = "ID del vehículo reservado",
        example = "3"
    )
    private Long idVehiculo;

    @Schema(
        description = "Estado actual de la reserva: CONFIRMADA, CANCELADA, EXPIRADA",
        example = "CONFIRMADA"
    )
    private String estado;
}