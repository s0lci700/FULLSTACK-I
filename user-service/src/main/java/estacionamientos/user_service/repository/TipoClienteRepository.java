package estacionamientos.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.user_service.model.TipoCliente;

public interface TipoClienteRepository extends JpaRepository<TipoCliente, Long> {

    boolean existsByNombre(String nombre);
}
