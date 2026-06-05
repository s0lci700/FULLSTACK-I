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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes de ocupación, accesos y cobros (solo lectura via Feign)")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Operation(summary = "Reporte de ocupación", description = "Retorna estadísticas de ocupación actual de espacios via Feign")
    @ApiResponse(responseCode = "200", description = "Datos de ocupación")
    @GetMapping("/ocupacion")
    public ResponseEntity<OcupacionReporteDTO> getOcupacion() {
        log.info("GET /api/reportes/ocupacion");
        return ResponseEntity.ok(reporteService.getOcupacion());
    }

    @Operation(summary = "Acceso por reserva", description = "Retorna el detalle del acceso asociado a una reserva")
    @ApiResponse(responseCode = "200", description = "Acceso encontrado")
    @ApiResponse(responseCode = "404", description = "No existe acceso para esa reserva")
    @GetMapping("/accesos/reserva/{idReserva}")
    public ResponseEntity<AccesoResponseDTO> getAccesosPorReserva(
        @PathVariable Long idReserva) {
        log.info("GET /api/reportes/accesos/reserva/{}", idReserva);
        return ResponseEntity.ok(reporteService.getAccesoByReserva(idReserva));
    }

    @Operation(summary = "Cobros por cliente", description = "Retorna el historial de cobros de un cliente específico")
    @ApiResponse(responseCode = "200", description = "Historial de cobros")
    @GetMapping("/cobros/cliente/{idCliente}")
    public ResponseEntity<List<CobroResponseDTO>> getCobrosPorCliente(
        @PathVariable Long idCliente) {
        log.info("GET /api/reportes/cobros/cliente/{}", idCliente);
        return ResponseEntity.ok(reporteService.getCobrosByCliente(idCliente));
    }

}
