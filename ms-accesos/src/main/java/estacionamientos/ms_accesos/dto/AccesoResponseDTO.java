package estacionamientos.ms_accesos.dto;

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

    private String fechaHoraEntrada;

    private String fechaHoraSalida;

    private String estado;
}
