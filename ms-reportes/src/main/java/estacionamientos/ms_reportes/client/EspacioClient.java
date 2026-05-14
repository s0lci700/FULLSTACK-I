package estacionamientos.ms_reportes.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import estacionamientos.ms_reportes.dto.EspacioResponseDTO;

@FeignClient(name = "ms-espacios")
public interface EspacioClient {
    @GetMapping("/api/espacios")
    List<EspacioResponseDTO> findAll();
}
