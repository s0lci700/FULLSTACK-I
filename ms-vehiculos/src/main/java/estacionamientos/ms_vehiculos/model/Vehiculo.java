
package estacionamientos.ms_vehiculos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "vehiculo")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String patente;

    @Column(nullable = false, unique = false)
    private String marca;

    @Column(nullable = false, unique = false)
    private String modelo;

    @Column(nullable = true, unique = false)
    private String color;

    @Column(nullable = true, unique = false)
    private Integer anio;

    // TODO: Reemplazar este Long por una relación JPA real.
    // TipoVehiculo está en la misma BD, por lo que sí debe haber FK real.
    // Cambiar a:
    // @ManyToOne
    // @JoinColumn(name = "id_tipo_vehiculo", nullable = false)
    // private TipoVehiculo tipoVehiculo;
    // @Column(nullable = false, unique = false)
    // private Long id_tipo_vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_tipo_vehiculo", nullable = false)
    private TipoVehiculo idTipoVehiculo;

    // id_cliente_ref es una FK lógica (apunta a db_usuarios que es otra BD).
    // No se puede hacer @ManyToOne entre BDs distintas — el Long está correcto
    // aquí.
    @Column(name = "id_cliente_ref", nullable = false)
    private Long idClienteRef;

    // TODO: Cambiar Integer por Boolean — activo es un flag true/false, no un
    // número.
    // Boolean mapea directamente a TINYINT(1) en MySQL, que es lo que define el
    // ERD.
    @Column(nullable = false, unique = false)
    private Boolean activo;
}
