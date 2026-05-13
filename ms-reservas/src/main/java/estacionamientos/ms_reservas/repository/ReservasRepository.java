package estacionamientos.ms_reservas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.ms_reservas.model.EstadoEnums;
import estacionamientos.ms_reservas.model.Reservas;

public interface ReservasRepository extends JpaRepository<Reservas, Long> {

    List<Reservas> findByIdCliente(Long idCliente);
    List<Reservas> findByIdEspacio(Long idEspacio);
    List<Reservas> findByEstado(EstadoEnums estado);
}
