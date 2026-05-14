package estacionamientos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_pagos.dto.VehiculoResponseDTO;

@FeignClient(name = "ms-vehiculos", contextId = "vehiculoClient")
public interface VehiculoClient {

    @GetMapping("/api/vehiculos/{id}")
    VehiculoResponseDTO getById(@PathVariable Long id);
}
