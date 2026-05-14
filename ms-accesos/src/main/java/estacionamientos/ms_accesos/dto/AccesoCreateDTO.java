package estacionamientos.ms_accesos.dto;

import java.time.LocalDateTime;

import estacionamientos.ms_accesos.model.EstadoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AccesoCreateDTO {

    @NotNull
    private Long idVehiculo;

    @NotNull
    private Long idEspacio;

    private Long idReserva;

    @NotBlank
    private String patenteEscaneada;

    @NotNull
    private LocalDateTime fechaHoraEntrada;

    private LocalDateTime fechaHoraSalida;

    @NotNull
    private EstadoEnum estado = EstadoEnum.ACTIVO;

}
