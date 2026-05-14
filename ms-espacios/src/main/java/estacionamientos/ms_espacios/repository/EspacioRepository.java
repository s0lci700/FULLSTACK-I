package estacionamientos.ms_espacios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import estacionamientos.ms_espacios.dto.EspacioUpdateDTO;
import estacionamientos.ms_espacios.model.Espacio;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Long>{
    List<Espacio> findByDisponibleTrue();
    boolean existsByNumero(String numero);
    Optional<Espacio> findByNumero(String numero);
    EspacioUpdateDTO encontrarPorId(Long id);
}
