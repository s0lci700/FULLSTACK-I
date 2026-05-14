package estacionamientos.ms_pagos.repository;

import estacionamientos.ms_pagos.model.Cobro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CobroRepository extends JpaRepository<Cobro, Long> {
    Optional<Cobro> findByIdAcceso(Long idAcceso);
    List<Cobro> findAllByMetodoPagoIdClienteRef(Long idClienteRef);
}