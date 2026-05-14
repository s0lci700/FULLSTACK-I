package estacionamientos.ms_pagos.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metodos_pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // ej: TARJETA, EFECTIVO, TRANSFERENCIA

    @ManyToOne
    @JoinColumn(name = "id_banco", nullable = true)
    private Banco banco;

    @ManyToOne
    @JoinColumn(name = "id_tipo_tarjeta", nullable = true)
    private TipoTarjeta tipoTarjeta;
}