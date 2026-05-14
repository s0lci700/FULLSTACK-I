package estacionamientos.ms_reportes.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_reportes.dto.CobroResponseDTO;

@FeignClient(name = "ms-cobros")
public interface CobroClient {
    @GetMapping("/api/cobros/cliente/{idCliente}")
    List<CobroResponseDTO> findByCliente(@PathVariable Long idCliente);

}
