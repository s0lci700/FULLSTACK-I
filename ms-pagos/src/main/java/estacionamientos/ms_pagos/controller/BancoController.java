package estacionamientos.ms_pagos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_pagos.dto.BancoDTO;
import estacionamientos.ms_pagos.dto.BancoResponseDTO;
import estacionamientos.ms_pagos.service.BancoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bancos")
@Tag(name = "Bancos", description = "Catálogo de bancos con descuentos asociados al pago")
public class BancoController {

    private final BancoService bancoService;

    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @Operation(summary = "Listar bancos", description = "Retorna todos los bancos registrados")
    @ApiResponse(responseCode = "200", description = "Listado de bancos")
    @GetMapping
    public ResponseEntity<List<BancoResponseDTO>> getAll() {
        log.info("GET /api/bancos");
        return ResponseEntity.ok(bancoService.findAll());
    }

    @Operation(summary = "Obtener banco", description = "Busca un banco por su ID")
    @ApiResponse(responseCode = "200", description = "Banco encontrado")
    @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<BancoResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/bancos/{}", id);
        return ResponseEntity.ok(bancoService.findById(id));
    }

    @Operation(summary = "Crear banco", description = "Registra un nuevo banco con su porcentaje de descuento")
    @ApiResponse(responseCode = "201", description = "Banco creado correctamente")
    @ApiResponse(responseCode = "409", description = "Nombre de banco duplicado")
    @PostMapping
    public ResponseEntity<BancoResponseDTO> create(@Valid @RequestBody BancoDTO dto) {
        log.info("POST /api/bancos");
        return ResponseEntity.status(HttpStatus.CREATED).body(bancoService.create(dto));
    }

    @Operation(summary = "Actualizar banco", description = "Actualiza nombre y descuento de un banco existente")
    @ApiResponse(responseCode = "200", description = "Banco actualizado")
    @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<BancoResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody BancoDTO dto) {
        log.info("PUT /api/bancos/{}", id);
        return ResponseEntity.ok(bancoService.update(id, dto));
    }

    @Operation(summary = "Eliminar banco", description = "Elimina un banco por su ID")
    @ApiResponse(responseCode = "204", description = "Banco eliminado")
    @ApiResponse(responseCode = "404", description = "Banco no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/bancos/{}", id);
        bancoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
