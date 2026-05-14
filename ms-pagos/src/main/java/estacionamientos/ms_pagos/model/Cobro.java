package estacionamientos.ms_pagos.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cobros")
public class Cobro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "id_acceso_ref")
    private Long idAcceso;

    @Column(nullable = false, name = "id_cliente_ref")
    private Long idCliente;

    @Column(nullable = false)
    private Long minutos; //falta que sol agregue los minutos en ms-accesos

    @Column(nullable = false)
    private Double montoBase;

    @Column(nullable = false)
    private Double montoFinal;

    @Column(nullable = false)
    private LocalDateTime fechaCobro;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;
}