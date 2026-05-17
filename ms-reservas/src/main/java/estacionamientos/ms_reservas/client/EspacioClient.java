package estacionamientos.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import estacionamientos.ms_reservas.dto.EspacioResponseDTO;

@FeignClient(name = "ms-espacios")
public interface EspacioClient {

    @GetMapping("/api/espacios/{id}")
    EspacioResponseDTO findById(@PathVariable Long id);

    @PutMapping("/api/espacios/{id}/disponibilidad")
    void updateDisponibilidad(@PathVariable Long id, @RequestParam Boolean disponible);

}
