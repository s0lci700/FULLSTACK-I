package estacionamientos.ms_vehiculos.repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.ms_vehiculos.model.Vehiculo;
import java.util.List;


public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPatente(String patente);

    

    List<Vehiculo> findByIdClienteRef(Long idClienteRef);


// 1. VehiculoRepository:17 — type mismatch
// List<Vehiculo> findByIdTipoVehiculo(Long idTipoVehiculo); // ← won't work
// The field in Vehiculo is now private TipoVehiculo idTipoVehiculo — Spring Data sees it as a TipoVehiculo, not a Long. You have two options:
// - Change the method to accept a TipoVehiculo: findByIdTipoVehiculo(TipoVehiculo tipo)
// - Or traverse into its id: findByIdTipoVehiculoId(Long id) (Spring Data supports nested property traversal)

// The second option is cleaner since callers can just pass a Long.
    List<Vehiculo> findByIdTipoVehiculoId(Long id);

}
