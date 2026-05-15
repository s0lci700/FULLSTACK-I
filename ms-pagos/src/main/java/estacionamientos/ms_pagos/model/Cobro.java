package estacionamientos.ms_pagos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cobro")
public class Cobro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "id_acceso_ref", unique = true)
    private Long idAcceso;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false, name = "id_tarifa_ref")
    private Long idTarifaRef;

    @Column(nullable = false)
    private Integer minutos;

    @Column(name = "monto_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoBase;

    @Column(name = "desc_tipo_cliente", nullable = false, precision = 5, scale = 2)
    private BigDecimal descTipoCliente;

    @Column(name = "desc_suscripcion", nullable = false, precision = 5, scale = 2)
    private BigDecimal descSuscripcion;

    @Column(name = "desc_banco", nullable = false, precision = 5, scale = 2)
    private BigDecimal descBanco;

    @Column(name = "monto_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoFinal;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaCobro;
}
