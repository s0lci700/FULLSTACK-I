package estacionamientos.security_service.repository;

import estacionamientos.security_service.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    // Verifica si ya existe un permiso con ese nombre
    // Lo usamos para no duplicar permisos en la base de datos
    boolean existsByNombre(String nombre);
}