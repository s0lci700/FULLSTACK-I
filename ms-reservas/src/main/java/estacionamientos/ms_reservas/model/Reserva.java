package estacionamientos.ms_reservas.model;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "reserva")
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "id_cliente_ref")
    private Long idCliente;

    @Column(nullable = false, name = "id_vehiculo_ref")
    private Long idVehiculo;

    @Column(nullable = false, name = "id_espacio_ref")
    private Long idEspacio;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnums estado = EstadoEnums.PENDIENTE;

    @CreationTimestamp
    @Column(nullable = true)
    private LocalDateTime fechaCreacion;

}
