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

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false, name = "id_tarifa_ref")
    private Long idTarifaRef;

    @Column(nullable = false)
    private Long minutos;

    @Column(nullable = false)
    private Double montoBase;

    @Column(name = "desc_tipo_cliente", nullable = false)
    private Double descTipoCliente;

    @Column(name = "desc_suscripcion", nullable = false)
    private Double descSuscripcion;

    @Column(name = "desc_banco", nullable = false)
    private Double descBanco;

    @Column(nullable = false)
    private Double montoFinal;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaCobro;
}
