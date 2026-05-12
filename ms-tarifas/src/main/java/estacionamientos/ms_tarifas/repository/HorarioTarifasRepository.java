package estacionamientos.ms_tarifas.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import estacionamientos.ms_tarifas.model.HorarioTarifas;

@Repository
public interface HorarioTarifasRepository extends JpaRepository<HorarioTarifas, Long> {

}
