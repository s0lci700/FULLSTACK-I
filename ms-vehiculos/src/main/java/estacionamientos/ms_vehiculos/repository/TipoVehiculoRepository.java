package estacionamientos.ms_vehiculos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.ms_vehiculos.model.TipoVehiculo;
import java.util.Optional;


public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {

    Optional<TipoVehiculo> findById(Long id);
}
