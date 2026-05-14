package estacionamientos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_pagos.dto.ClienteResponseDTO;

@FeignClient(name = "user-service")
public interface ClienteClient {

    @GetMapping("/api/clientes/{id}")
    ClienteResponseDTO getById(@PathVariable Long id);
}