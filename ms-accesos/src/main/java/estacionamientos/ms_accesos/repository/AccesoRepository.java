package estacionamientos.ms_accesos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.ms_accesos.model.Acceso;
import estacionamientos.ms_accesos.model.EstadoEnum;

public interface AccesoRepository extends JpaRepository<Acceso, Long> {
    List<Acceso> findByIdVehiculo(Long idVehiculo);
    List<Acceso> findByIdEspacio(Long idEspacio);
    List<Acceso> findByIdReserva(Long idReserva);
    List<Acceso> findByEstado(EstadoEnum estado);
}
