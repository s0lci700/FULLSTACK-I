package estacionamientos.ms_reportes.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AccesoResponseDTO {
    private Long id;
    private Long idVehiculo;
    private Long idEspacio;
    private Long idReserva;
    private String patenteEscaneada;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private Long minutos;
    private String estado;

}
