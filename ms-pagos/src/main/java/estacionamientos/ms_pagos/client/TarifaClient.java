package estacionamientos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import estacionamientos.ms_pagos.dto.TarifaResponseDTO;

@FeignClient(name = "ms-tarifas")
public interface TarifaClient {

    @GetMapping("/api/tarifas/vigente")
    TarifaResponseDTO getTarifaVigente();
}