package estacionamientos.ms_accesos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AccesoCreateDTO {

    @NotNull(message = "El id de la reserva es obligatorio")
    @Schema(description = "ID de la reserva asociada", example = "1")
    private Long idReserva;

    @NotNull(message = "El id del vehículo es obligatorio")
    @Schema(description = "ID del vehículo que ingresa", example = "1")
    private Long idVehiculo;

    @NotBlank(message = "La patente escaneada es obligatoria")
    @Schema(description = "Patente escaneada en la barrera", example = "ABCD12")
    private String patenteEscaneada;
    
}
