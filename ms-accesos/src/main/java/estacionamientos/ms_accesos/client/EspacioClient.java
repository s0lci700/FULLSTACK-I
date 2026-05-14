package estacionamientos.ms_accesos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-espacios")
public interface EspacioClient {
    @PatchMapping("/api/espacios/{id}/available")
    void updateDisponibilidad(@PathVariable Long id, @RequestParam boolean disponible);
}
