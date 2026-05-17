package estacionamientos.ms_accesos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-espacios")
public interface EspacioClient {
    @PutMapping("/api/espacios/{id}/disponibilidad")
    void updateDisponibilidad(@PathVariable Long id, @RequestParam boolean disponible);
}
