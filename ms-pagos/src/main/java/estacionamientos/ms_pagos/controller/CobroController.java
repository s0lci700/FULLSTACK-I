package estacionamientos.ms_pagos.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_pagos.dto.CobroCreateDTO;
import estacionamientos.ms_pagos.dto.CobroResponseDTO;
import estacionamientos.ms_pagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cobros")
public class CobroController {


    private final PagoService pagoService;

    public CobroController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<List<CobroResponseDTO>> getAll() {
        log.info("GET /api/cobros");
        return ResponseEntity.ok(pagoService.findAll());
    }

    // Genera un nuevo cobro a partir de un acceso completado
    // Consulta via Feign: acceso, tarifa vigente y cliente
    // Retorna 201 CREATED con el cobro generado
    @PostMapping
    public ResponseEntity<CobroResponseDTO> crear(@Valid @RequestBody CobroCreateDTO dto) {
        log.info("POST /api/cobros");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crear(dto));
    }

    // Busca un cobro por su ID
    // Retorna 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<CobroResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/cobros/{}", id);
        return ResponseEntity.ok(pagoService.findById(id));
    }

    // Busca el cobro asociado a un acceso especifico
    // Util para saber cuanto se cobro al cerrar un acceso
    @GetMapping("/acceso/{accesoId}")
    public ResponseEntity<CobroResponseDTO> getByAcceso(@PathVariable Long accesoId) {
        log.info("GET /api/cobros/acceso/{}", accesoId);
        return ResponseEntity.ok(pagoService.findByIdAcceso(accesoId));
    }

    // Retorna todos los cobros de un cliente especifico
    // Util para historial de pagos del cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CobroResponseDTO>> getByCliente(@PathVariable Long clienteId) {
        log.info("GET /api/cobros/cliente/{}", clienteId);
        return ResponseEntity.ok(pagoService.findByIdCliente(clienteId));
    }
}
