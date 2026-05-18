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

import estacionamientos.ms_pagos.dto.MetodoPagoCreateDTO;
import estacionamientos.ms_pagos.dto.MetodoPagoResponseDTO;
import estacionamientos.ms_pagos.service.MetodoPagoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/metodos-pago")
@Slf4j
public class MetodoPagoController {


    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    // Retorna la lista completa de metodos de pago disponibles
    @GetMapping
    public ResponseEntity<List<MetodoPagoResponseDTO>> getAll() {
        log.info("GET /api/metodos-pago");
        return ResponseEntity.ok(metodoPagoService.findAll());
    }

    // Busca un metodo de pago por su ID
    // Retorna 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/metodos-pago/{}", id);
        return ResponseEntity.ok(metodoPagoService.findById(id));
    }

    // Crea un nuevo metodo de pago asociado opcionalmente a un banco y tipo de tarjeta
    // Si se indica idBanco o idTipoTarjeta y no existen, retorna 404
    // Retorna 201 CREATED con el metodo creado
    @PostMapping
    public ResponseEntity<MetodoPagoResponseDTO> create(@Valid @RequestBody MetodoPagoCreateDTO dto) {
        log.info("POST /api/metodos-pago");
        return ResponseEntity.status(HttpStatus.CREATED).body(metodoPagoService.create(dto));
    }

    // Elimina un metodo de pago por su ID
    // Retorna 204 sin contenido si se elimino correctamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/metodos-pago/{}", id);
        metodoPagoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
