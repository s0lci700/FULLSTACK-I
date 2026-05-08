package example.ms_pedidos.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productoId; // Referencia al producto del otro MS
    private Integer cantidad;
    private String estado; // EJ: "PENDIENTE", "COMPLETADO"
}
