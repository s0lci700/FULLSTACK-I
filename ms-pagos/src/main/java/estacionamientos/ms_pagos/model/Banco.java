package estacionamientos.ms_pagos.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "banco")
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(name = "descuento_pct", precision = 5, scale = 2)
    private BigDecimal descuentoPct;

    @Column(nullable = false)
    private Boolean activo = true;
}
