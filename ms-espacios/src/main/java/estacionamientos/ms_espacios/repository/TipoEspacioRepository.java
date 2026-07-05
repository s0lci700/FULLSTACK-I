package estacionamientos.ms_espacios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import estacionamientos.ms_espacios.model.TipoEspacio;

@Repository
public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Long>{

    boolean existsByNombre(String nombre);
}
