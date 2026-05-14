package estacionamientos.ms_pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import estacionamientos.ms_pagos.dto.HorarioTarifaResponseDTO;

@FeignClient(name = "ms-tarifas", contextId = "horarioTarifaClient")
public interface HorarioTarifaClient {

    @GetMapping("/api/horarios-tarifa/vigente")
    HorarioTarifaResponseDTO getVigente();
}
