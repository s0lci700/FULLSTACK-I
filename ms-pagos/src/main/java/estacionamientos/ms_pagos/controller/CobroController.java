package estacionamientos.ms_pagos.controller;

import java.util.List;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cobros")
@Tag(name = "Cobros", description = "Facturación de accesos completados en el estacionamiento")
public class CobroController {

    private final PagoService pagoService;

    public CobroController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(summary = "Listar cobros", description = "Retorna todos los cobros registrados")
    @ApiResponse(responseCode = "200", description = "Listado de cobros")
    @GetMapping
    public ResponseEntity<List<CobroResponseDTO>> getAll() {
        log.info("GET /api/cobros");
        return ResponseEntity.ok(pagoService.findAll());
    }

    @Operation(summary = "Generar cobro", description = "Genera un cobro a partir de un acceso completado. Aplica fórmula con tarifas, cliente y vehículo via Feign.")
    @ApiResponse(responseCode = "201", description = "Cobro generado correctamente")
    @ApiResponse(responseCode = "404", description = "Acceso o tarifa no encontrada")
    @ApiResponse(responseCode = "422", description = "Acceso no completado o cobro ya existe")
    @PostMapping
    public ResponseEntity<CobroResponseDTO> crear(@Valid @RequestBody CobroCreateDTO dto) {
        log.info("POST /api/cobros");
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.crear(dto));
    }

    @Operation(summary = "Obtener cobro", description = "Busca un cobro por su ID")
    @ApiResponse(responseCode = "200", description = "Cobro encontrado")
    @ApiResponse(responseCode = "404", description = "Cobro no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<CobroResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/cobros/{}", id);
        return ResponseEntity.ok(pagoService.findById(id));
    }

    @Operation(summary = "Cobro por acceso", description = "Retorna el cobro asociado a un acceso específico")
    @ApiResponse(responseCode = "200", description = "Cobro encontrado")
    @ApiResponse(responseCode = "404", description = "No existe cobro para ese acceso")
    @GetMapping("/acceso/{accesoId}")
    public ResponseEntity<CobroResponseDTO> getByAcceso(@PathVariable Long accesoId) {
        log.info("GET /api/cobros/acceso/{}", accesoId);
        return ResponseEntity.ok(pagoService.findByIdAcceso(accesoId));
    }

    @Operation(summary = "Cobros por cliente", description = "Retorna el historial de cobros de un cliente")
    @ApiResponse(responseCode = "200", description = "Historial de cobros del cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CobroResponseDTO>> getByCliente(@PathVariable Long clienteId) {
        log.info("GET /api/cobros/cliente/{}", clienteId);
        return ResponseEntity.ok(pagoService.findByIdCliente(clienteId));
    }
}
