package estacionamientos.ms_pagos.repository;

import estacionamientos.ms_pagos.model.TipoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoTarjetaRepository extends JpaRepository<TipoTarjeta, Long> {
    boolean existsByNombre(String nombre);
}