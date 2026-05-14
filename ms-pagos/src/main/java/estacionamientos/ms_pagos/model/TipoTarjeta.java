package estacionamientos.ms_pagos.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipo_tarjeta")
public class TipoTarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String red;
}