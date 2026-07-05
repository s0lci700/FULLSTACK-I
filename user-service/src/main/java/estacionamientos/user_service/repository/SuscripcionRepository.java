package estacionamientos.user_service.repository;

import estacionamientos.user_service.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    boolean existsByNombre(String nombre);
}