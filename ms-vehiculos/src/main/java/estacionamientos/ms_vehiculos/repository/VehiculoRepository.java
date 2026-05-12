package estacionamientos.ms_vehiculos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.ms_vehiculos.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPatente(String patente);

   Optional<Vehiculo> findById(Long id);

}
