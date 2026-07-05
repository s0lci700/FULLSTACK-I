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

import estacionamientos.ms_pagos.dto.MetodoPagoCreateDTO;
import estacionamientos.ms_pagos.dto.MetodoPagoResponseDTO;
import estacionamientos.ms_pagos.dto.MetodoPagoUpdateDTO;
import estacionamientos.ms_pagos.service.MetodoPagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/metodos-pago")
@Slf4j
@Tag(name = "Métodos de Pago", description = "Registro de métodos de pago (banco + tipo de tarjeta)")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    @Operation(summary = "Listar métodos de pago", description = "Retorna todos los métodos de pago disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de métodos de pago")
    @GetMapping
    public ResponseEntity<List<MetodoPagoResponseDTO>> getAll() {
        log.info("GET /api/metodos-pago");
        return ResponseEntity.ok(metodoPagoService.findAll());
    }

    @Operation(summary = "Obtener método de pago", description = "Busca un método de pago por su ID")
    @ApiResponse(responseCode = "200", description = "Método encontrado")
    @ApiResponse(responseCode = "404", description = "Método no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/metodos-pago/{}", id);
        return ResponseEntity.ok(metodoPagoService.findById(id));
    }

    @Operation(summary = "Crear método de pago", description = "Registra un nuevo método de pago, asociado opcionalmente a banco y tipo de tarjeta")
    @ApiResponse(responseCode = "201", description = "Método de pago creado correctamente")
    @ApiResponse(responseCode = "404", description = "Banco o tipo de tarjeta no encontrado")
    @PostMapping
    public ResponseEntity<MetodoPagoResponseDTO> create(@Valid @RequestBody MetodoPagoCreateDTO dto) {
        log.info("POST /api/metodos-pago");
        return ResponseEntity.status(HttpStatus.CREATED).body(metodoPagoService.create(dto));
    }

    @Operation(summary = "Actualizar método de pago", description = "Actualiza los datos de un método de pago existente")
    @ApiResponse(responseCode = "200", description = "Método actualizado")
    @ApiResponse(responseCode = "404", description = "Método, banco o tipo de tarjeta no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<MetodoPagoResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody MetodoPagoUpdateDTO dto) {
        log.info("PUT /api/metodos-pago/{}", id);
        return ResponseEntity.ok(metodoPagoService.update(id, dto));
    }

    @Operation(summary = "Eliminar método de pago", description = "Elimina un método de pago por su ID")
    @ApiResponse(responseCode = "204", description = "Método eliminado")
    @ApiResponse(responseCode = "404", description = "Método no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/metodos-pago/{}", id);
        metodoPagoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
