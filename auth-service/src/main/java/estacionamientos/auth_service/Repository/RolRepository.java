package estacionamientos.auth_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.auth_service.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

}
