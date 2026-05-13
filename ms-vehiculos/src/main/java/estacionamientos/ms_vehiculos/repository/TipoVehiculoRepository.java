package estacionamientos.ms_vehiculos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.ms_vehiculos.model.TipoVehiculo;


public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {

    public Optional<TipoVehiculo> findByNombre(String nombre);
}
