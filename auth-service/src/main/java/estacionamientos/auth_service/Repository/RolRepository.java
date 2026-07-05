package estacionamientos.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.auth_service.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
