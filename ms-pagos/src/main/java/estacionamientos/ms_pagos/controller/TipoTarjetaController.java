package estacionamientos.ms_pagos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_pagos.dto.TipoTarjetaDTO;
import estacionamientos.ms_pagos.dto.TipoTarjetaResponseDTO;
import estacionamientos.ms_pagos.service.TipoTarjetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/tipo-tarjetas")
@Tag(name = "Tipos de Tarjeta", description = "Catálogo de tipos de tarjeta de pago (DÉBITO, CRÉDITO, etc.)")
public class TipoTarjetaController {

    private final TipoTarjetaService tipoTarjetaService;

    public TipoTarjetaController(TipoTarjetaService tipoTarjetaService) {
        this.tipoTarjetaService = tipoTarjetaService;
    }

    @Operation(summary = "Listar tipos de tarjeta", description = "Retorna el catálogo de tipos de tarjeta")
    @ApiResponse(responseCode = "200", description = "Listado de tipos de tarjeta")
    @GetMapping
    public ResponseEntity<List<TipoTarjetaResponseDTO>> getAll() {
        log.info("GET /api/tipo-tarjetas");
        return ResponseEntity.ok(tipoTarjetaService.findAll());
    }

    @Operation(summary = "Obtener tipo de tarjeta", description = "Busca un tipo de tarjeta por su ID")
    @ApiResponse(responseCode = "200", description = "Tipo encontrado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<TipoTarjetaResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tipo-tarjetas/{}", id);
        return ResponseEntity.ok(tipoTarjetaService.findById(id));
    }

    @Operation(summary = "Crear tipo de tarjeta", description = "Registra un nuevo tipo de tarjeta (ej: DÉBITO, CRÉDITO)")
    @ApiResponse(responseCode = "201", description = "Tipo de tarjeta creado correctamente")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PostMapping
    public ResponseEntity<TipoTarjetaResponseDTO> create(@Valid @RequestBody TipoTarjetaDTO dto) {
        log.info("POST /api/tipo-tarjetas");
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoTarjetaService.create(dto));
    }

    @Operation(summary = "Eliminar tipo de tarjeta", description = "Elimina un tipo de tarjeta por su ID")
    @ApiResponse(responseCode = "204", description = "Tipo eliminado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tipo-tarjetas/{}", id);
        tipoTarjetaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
