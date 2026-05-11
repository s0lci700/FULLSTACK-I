package estacionamientos.ms_espacios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import estacionamientos.ms_espacios.model.Espacios;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspaciosRepository extends JpaRepository<Espacios, Long>{
    List<Espacios> findByDisponibleTrue();
    boolean existsByNumero(String numero);
    Optional<Espacios> findByNumero(String numero);
}
