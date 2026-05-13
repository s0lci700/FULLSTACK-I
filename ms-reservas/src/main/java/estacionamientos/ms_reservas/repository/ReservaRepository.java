package estacionamientos.ms_reservas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import estacionamientos.ms_reservas.model.EstadoEnums;
import estacionamientos.ms_reservas.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByIdCliente(Long idCliente);
    List<Reserva> findByIdEspacio(Long idEspacio);
    List<Reserva> findByEstado(EstadoEnums estado);
}
