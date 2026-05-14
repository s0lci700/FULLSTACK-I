package estacionamientos.ms_reportes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.ms_reportes.client.AccesoClient;
import estacionamientos.ms_reportes.client.CobroClient;
import estacionamientos.ms_reportes.client.EspacioClient;
import estacionamientos.ms_reportes.dto.AccesoResponseDTO;
import estacionamientos.ms_reportes.dto.CobroResponseDTO;
import estacionamientos.ms_reportes.dto.EspacioResponseDTO;
import estacionamientos.ms_reportes.dto.OcupacionReporteDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReporteService {

    @Autowired
    private AccesoClient accesoClient;

    @Autowired
    private EspacioClient espacioClient;

    @Autowired
    private CobroClient cobroClient;

    public OcupacionReporteDTO getOcupacion() {
        log.info("Consultando ocupación de espacios");
        List<EspacioResponseDTO> espacios = espacioClient.findAll();
        int totalEspacios = espacios.size();
        int disponibles = (int) espacios.stream()
                .filter(e -> Boolean.TRUE.equals(e.getDisponible())
                        && Boolean.TRUE.equals(e.getActivo()))
                .count();
        int ocupados = totalEspacios - disponibles;
        return new OcupacionReporteDTO(totalEspacios, disponibles, ocupados);
    }

    public AccesoResponseDTO getAccesoByReserva(Long idReserva) {
        log.info("Consultando acceso para reserva id={}", idReserva);
        return accesoClient.findByReserva(idReserva);
    }

    public List<CobroResponseDTO> getCobrosByCliente(Long idCliente) {
        log.info("Consultando cobros para cliente id={}", idCliente);
        return cobroClient.findByCliente(idCliente);
    }
}
