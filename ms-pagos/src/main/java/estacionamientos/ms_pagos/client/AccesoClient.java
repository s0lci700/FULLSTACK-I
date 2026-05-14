package estacionamientos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_pagos.dto.AccesoResponseDTO;


@FeignClient(name = "ms-accesos")
public interface AccesoClient {

    @GetMapping("/api/accesos/{id}")
    AccesoResponseDTO getById(@PathVariable Long id);
}