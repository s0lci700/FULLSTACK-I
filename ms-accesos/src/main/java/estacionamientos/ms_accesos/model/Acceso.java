package estacionamientos.ms_accesos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor @AllArgsConstructor
@Table(name = "accesos")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "id_vehiculo_ref")
    private Long idVehiculo;

    @Column(nullable = false, name = "id_espacio_ref")
    private Long idEspacio;

    @Column(nullable = true, name = "id_reserva_ref")
    private Long idReserva;

    @Column(nullable = false)
    private String patenteEscaneada;

    @Column(nullable = false)
    private LocalDateTime fechaHoraEntrada;

    @Column(nullable = true)
    private LocalDateTime fechaHoraSalida;

    @Column(nullable = false)
    private EstadoEnum estado = EstadoEnum.ACTIVO;
}
