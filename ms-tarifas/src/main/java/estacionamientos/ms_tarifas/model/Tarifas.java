package estacionamientos.ms_tarifas.model;

import java.math.BigDecimal;

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
@Table(name = "tarifa")

public class Tarifas {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique = true)
    private String nombre;

    @Column(nullable=true)
    private String descripcion;

    @Column(name= "precio_base_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBaseHora;

    @Column(nullable= false)
    private Boolean activo;
}   
