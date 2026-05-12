package estacionamientos.user_service.repository;

import estacionamientos.user_service.model.ClienteSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteSuscripcionRepository extends JpaRepository<ClienteSuscripcion, Long> {
    List<ClienteSuscripcion> findByClienteId(Long clienteId);
}