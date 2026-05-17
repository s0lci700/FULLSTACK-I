package estacionamientos.ms_tarifas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import estacionamientos.ms_tarifas.model.Tarifas;

@Repository
public interface TarifasRepository extends JpaRepository<Tarifas, Long>{
    boolean existsByNombre(String nombre);
    Optional<Tarifas> findFirstByActivoTrue();
    List<Tarifas> findAllByActivoTrue();
}
