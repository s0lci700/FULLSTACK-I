package estacionamientos.ms_accesos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AccesoCreateDTO {

    @NotNull(message = "El id de la reserva es obligatorio")
    private Long idReserva;

    @NotNull(message = "El id del vehículo es obligatorio")
    private Long idVehiculo;

    @NotBlank(message = "La patente escaneada es obligatoria")
    private String patenteEscaneada;
    
}
