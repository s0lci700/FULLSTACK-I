package example.ms_administrador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data @Entity
@Table(name = "productos")
public class Producto {
    @Id
    private Long id; // ID manual (ej: SKU)
    private String nombre;
    private String descripcion;
    private Double precioUnitario;
    private String categoria;
}
