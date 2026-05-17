package estacionamientos.security_service.repository;

import estacionamientos.security_service.model.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    // Busca todos los permisos asignados a un rol especifico
    // Lo usamos para saber que puede hacer cada rol
    List<RolPermiso> findAllByIdRol(Long idRol);

    // Verifica si un rol ya tiene asignado ese permiso
    // Lo usamos para no asignar el mismo permiso dos veces al mismo rol
    boolean existsByIdRolAndPermisoId(Long idRol, Long permisoId);

    List<RolPermiso> findAllByPermisoId(Long permisoId);
}