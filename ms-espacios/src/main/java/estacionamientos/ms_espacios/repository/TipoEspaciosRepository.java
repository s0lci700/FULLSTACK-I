package estacionamientos.ms_espacios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import estacionamientos.ms_espacios.model.TipoEspacios;

@Repository
public interface TipoEspaciosRepository extends JpaRepository<TipoEspacios, Long>{

}
