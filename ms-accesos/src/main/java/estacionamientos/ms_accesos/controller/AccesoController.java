package estacionamientos.ms_accesos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_accesos.dto.AccesoCreateDTO;
import estacionamientos.ms_accesos.dto.AccesoResponseDTO;
import estacionamientos.ms_accesos.service.AccesoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/accesos")
public class AccesoController {

    @Autowired
    private AccesoService accesoService;

    @PostMapping("/entrada")
    public ResponseEntity<AccesoResponseDTO> registrarEntrada(
        @Valid @RequestBody AccesoCreateDTO dto) {
            log.info("POST /api/accesos/entrada - idReserva={}", dto.getIdReserva());
            return ResponseEntity.ok(accesoService.registrarEntrada(dto));
    }

    @PatchMapping("/{id}/salida")
    public ResponseEntity<AccesoResponseDTO> registrarSalida(
        @PathVariable Long id) {
        log.info("PATCH /api/accesos/{}/salida", id);
        return ResponseEntity.ok(accesoService.registrarSalida(id));
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<AccesoResponseDTO> getByReserva(
        @PathVariable Long idReserva) {
        log.info("GET /api/accesos/reserva/{}", idReserva);
        return ResponseEntity.ok(accesoService.findByReserva(idReserva));
    }

}
