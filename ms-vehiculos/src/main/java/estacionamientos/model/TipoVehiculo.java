package estacionamientos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data @Entity
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
    @Column(nullable = false, unique = false)
    private Float factor_precio;
}
