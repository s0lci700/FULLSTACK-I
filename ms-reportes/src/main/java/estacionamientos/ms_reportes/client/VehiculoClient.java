package estacionamientos.ms_reportes.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_reportes.dto.VehiculoResponseDTO;

@FeignClient(name = "ms-vehiculos")
public interface VehiculoClient {

    @GetMapping("/api/vehiculos/{id}")
    VehiculoResponseDTO findById(@PathVariable Long id);

    @GetMapping("/api/vehiculos")
    List<VehiculoResponseDTO> findAll();
}
