package estacionamientos.ms_reservas.dto;


import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReservaCreateDTO {

    @NotNull
    private Long idCliente;
    @NotNull
    private Long idVehiculo;
    @NotNull
    private Long idEspacio;
    @NotNull
    private LocalDateTime fechaInicio;
    @NotNull
    private LocalDateTime fechaFin;
}
