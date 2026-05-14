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

    @Column(name = "id_cliente_ref", nullable = false)
    private Long idClienteRef;

    @ManyToOne
    @JoinColumn(name = "id_tipo_tarjeta", nullable = false)
    private TipoTarjeta tipoTarjeta;

    @ManyToOne
    @JoinColumn(name = "id_banco", nullable = false)
    private Banco banco;

    @Column(name = "ultimos_4", nullable = false, length = 4)
    private String ultimos4;

    @Column(name = "nombre_titular", nullable = false)
    private String nombreTitular;

    @Column(name = "mes_vencimiento", nullable = false)
    private Integer mesVencimiento;

    @Column(name = "anio_vencimiento", nullable = false)
    private Integer anioVencimiento;

    @Column(nullable = false)
    private Boolean predeterminado;

    @Column(nullable = false)
    private Boolean activo;
}
