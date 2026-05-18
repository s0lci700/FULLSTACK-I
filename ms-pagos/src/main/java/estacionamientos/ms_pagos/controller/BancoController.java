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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bancos")
public class BancoController {


    private final BancoService bancoService;

    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    // Retorna la lista completa de bancos registrados
    @GetMapping
    public ResponseEntity<List<BancoResponseDTO>> getAll() {
        log.info("GET /api/bancos");
        return ResponseEntity.ok(bancoService.findAll());
    }

    // Busca un banco por su ID
    // Retorna 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<BancoResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/bancos/{}", id);
        return ResponseEntity.ok(bancoService.findById(id));
    }

    // Crea un nuevo banco con su porcentaje de descuento
    // Valida que no exista otro banco con el mismo nombre
    // Retorna 201 CREATED con el banco creado
    @PostMapping
    public ResponseEntity<BancoResponseDTO> create(@Valid @RequestBody BancoDTO dto) {
        log.info("POST /api/bancos");
        return ResponseEntity.status(HttpStatus.CREATED).body(bancoService.create(dto));
    }

    // Actualiza el nombre y descuento de un banco existente
    // Retorna 404 si no existe
    @PutMapping("/{id}")
    public ResponseEntity<BancoResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody BancoDTO dto) {
        log.info("PUT /api/bancos/{}", id);
        return ResponseEntity.ok(bancoService.update(id, dto));
    }

    // Elimina un banco por su ID
    // Retorna 204 sin contenido si se elimino correctamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/bancos/{}", id);
        bancoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}