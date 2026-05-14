package estacionamientos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_pagos.dto.EspacioResponseDTO;

@FeignClient(name = "ms-espacios")
public interface EspacioClient {

    @GetMapping("/api/espacios/{id}")
    EspacioResponseDTO getById(@PathVariable Long id);
}
