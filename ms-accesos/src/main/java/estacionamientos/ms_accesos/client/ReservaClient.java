package estacionamientos.ms_accesos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_accesos.dto.ReservaResponseDTO;

@FeignClient(name = "ms-reservas")
public interface ReservaClient {

    @GetMapping("/api/reservas/{id}")
    ReservaResponseDTO findById(@PathVariable Long id);

    @PutMapping("/api/reservas/{id}/finalizar")
    void finalizarReserva(@PathVariable Long id);
}
