package estacionamientos.ms_pagos.repository;

import estacionamientos.ms_pagos.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {
    boolean existsByNombre(String nombre);
}