package estacionamientos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPatente(String patente);

}
