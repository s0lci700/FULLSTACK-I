package estacionamientos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_pagos.dto.TipoVehiculoResponseDTO;

@FeignClient(name = "ms-vehiculos", contextId = "tipoVehiculoClient")
public interface TipoVehiculoClient {

    @GetMapping("/api/tipos-vehiculo/{id}")
    TipoVehiculoResponseDTO getById(@PathVariable Long id);
}
