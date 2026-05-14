package estacionamientos.ms_reportes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import estacionamientos.ms_reportes.dto.AccesoResponseDTO;

@FeignClient(name = "ms-accesos")
public interface AccesoClient {
    @GetMapping("api/accesos/reserva/{idReserva}")
    AccesoResponseDTO findByReserva(@PathVariable Long idReserva);

}
