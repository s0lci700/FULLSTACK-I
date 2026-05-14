package estacionamientos.ms_reportes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_reportes.dto.AccesoResponseDTO;
import estacionamientos.ms_reportes.dto.CobroResponseDTO;
import estacionamientos.ms_reportes.dto.OcupacionReporteDTO;
import estacionamientos.ms_reportes.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/ocupacion")
    public ResponseEntity<OcupacionReporteDTO> getOcupacion() {
        return ResponseEntity.ok(reporteService.getOcupacion());
    }

    @GetMapping("/accesos/reserva/{idReserva}")
    public ResponseEntity<AccesoResponseDTO> getAccesosPorReserva(
        @PathVariable Long idReserva) {
        return ResponseEntity.ok(reporteService.getAccesoByReserva(idReserva));
    }

    @GetMapping("/cobros/cliente/{idCliente}")
    public ResponseEntity<List<CobroResponseDTO>> getCobrosPorCliente(
        @PathVariable Long idCliente) {
        return ResponseEntity.ok(reporteService.getCobrosByCliente(idCliente));
    }

}
