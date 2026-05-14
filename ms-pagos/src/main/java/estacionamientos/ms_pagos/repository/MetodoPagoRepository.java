package estacionamientos.ms_pagos.repository;

import estacionamientos.ms_pagos.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
}
