package estacionamientos.ms_pagos.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/tipo-tarjetas")
public class TipoTarjetaController {


    private final TipoTarjetaService tipoTarjetaService;

    public TipoTarjetaController(TipoTarjetaService tipoTarjetaService) {
        this.tipoTarjetaService = tipoTarjetaService;
    }

    // Retorna la lista completa de tipos de tarjeta registrados
    @GetMapping
    public ResponseEntity<List<TipoTarjetaResponseDTO>> getAll() {
        log.info("GET /api/tipo-tarjetas");
        return ResponseEntity.ok(tipoTarjetaService.findAll());
    }

    // Busca un tipo de tarjeta por su ID
    // Retorna 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<TipoTarjetaResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tipo-tarjetas/{}", id);
        return ResponseEntity.ok(tipoTarjetaService.findById(id));
    }

    // Crea un nuevo tipo de tarjeta (ej: DEBITO, CREDITO)
    // Valida que no exista otro con el mismo nombre
    // Retorna 201 CREATED con el tipo creado
    @PostMapping
    public ResponseEntity<TipoTarjetaResponseDTO> create(@Valid @RequestBody TipoTarjetaDTO dto) {
        log.info("POST /api/tipo-tarjetas");
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoTarjetaService.create(dto));
    }

    // Elimina un tipo de tarjeta por su ID
    // Retorna 204 sin contenido si se elimino correctamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tipo-tarjetas/{}", id);
        tipoTarjetaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
