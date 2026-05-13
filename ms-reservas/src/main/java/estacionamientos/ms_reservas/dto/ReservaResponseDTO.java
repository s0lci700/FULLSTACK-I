package estacionamientos.ms_reservas.dto;

import java.time.LocalDateTime;

import estacionamientos.ms_reservas.model.EstadoEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReservaResponseDTO {

    private Long id;
    private Long idCliente;
    private Long idVehiculo;
    private Long idEspacio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoEnums estado;
    private LocalDateTime fechaCreacion; 
}
