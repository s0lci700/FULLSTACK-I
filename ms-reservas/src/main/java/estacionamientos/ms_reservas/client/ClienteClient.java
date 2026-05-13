package estacionamientos.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_reservas.dto.ClienteResponseDTO;

@FeignClient(name = "user-service")
public interface ClienteClient {
    @GetMapping("/api/clientes/{id}")
    ClienteResponseDTO findById(@PathVariable Long id);
}
