package estacionamientos.ms_vehiculos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data @Entity
@Table(name = "tipo_vehiculo")
public class TipoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = true, unique = false)
    private String descripcion;

    // TODO: Cambiar Float por BigDecimal — factor_precio se usa en la fórmula de cobro
    // (multiplicación con precios), Float pierde precisión con decimales.
    // También renombrar a factorPrecio (camelCase) para seguir la convención Java;
    // usar @Column(name = "factor_precio") para mantener el nombre de columna en la BD.
    @Column(name = "factor_precio", nullable = false)
    private Double factorPrecio;
}
