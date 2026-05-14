package estacionamientos.security_service.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rol_permiso")
public class RolPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK logica al rol que vive en auth-service
    @Column(nullable = false, name = "id_rol_ref")
    private Long idRol;

    @ManyToOne
    @JoinColumn(name = "id_permiso", nullable = false)
    private Permiso permiso;
}