package estacionamientos.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_reservas.dto.VehiculoResponseDTO;

@FeignClient(name = "ms-vehiculos")
public interface VehiculoClient {

    @GetMapping("/api/vehiculos/{id}")
    VehiculoResponseDTO findById(@PathVariable Long id);
}
