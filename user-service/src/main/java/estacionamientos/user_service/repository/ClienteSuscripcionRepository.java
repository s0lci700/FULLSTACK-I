package estacionamientos.user_service.repository;

import estacionamientos.user_service.model.ClienteSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteSuscripcionRepository extends JpaRepository<ClienteSuscripcion, Long> {
    List<ClienteSuscripcion> findByClienteId(Long clienteId);

    boolean existsByClienteIdAndSuscripcionIdAndActivoTrue(Long clienteId, Long suscripcionId);
}