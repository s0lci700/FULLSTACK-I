package estacionamientos.user_service.controller;

import estacionamientos.user_service.dto.SuscripcionCreateDTO;
import estacionamientos.user_service.dto.SuscripcionResponseDTO;
import estacionamientos.user_service.dto.SuscripcionUpdateDTO;
import estacionamientos.user_service.service.SuscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/suscripciones")
@Tag(name = "Suscripciones", description = "Catálogo de tipos de suscripción disponibles")
public class SuscripcionController {

    @Autowired
    private SuscripcionService suscripcionService;

    @Operation(summary = "Listar suscripciones", description = "Retorna el catálogo completo de suscripciones disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de suscripciones")
    @GetMapping
    public ResponseEntity<List<SuscripcionResponseDTO>> getAll() {
        log.info("GET /api/suscripciones");
        return ResponseEntity.ok(suscripcionService.findAll());
    }

    @Operation(summary = "Obtener suscripción", description = "Busca una suscripción por su ID")
    @ApiResponse(responseCode = "200", description = "Suscripción encontrada")
    @ApiResponse(responseCode = "404", description = "Suscripción no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/suscripciones/{}", id);
        return ResponseEntity.ok(suscripcionService.findById(id));
    }

    @Operation(summary = "Crear suscripción", description = "Registra un nuevo tipo de suscripción")
    @ApiResponse(responseCode = "201", description = "Suscripción creada correctamente")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> create(@Valid @RequestBody SuscripcionCreateDTO dto) {
        log.info("POST /api/suscripciones");
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcionService.create(dto));
    }

    @Operation(summary = "Actualizar suscripción", description = "Actualiza los datos de una suscripción existente")
    @ApiResponse(responseCode = "200", description = "Suscripción actualizada")
    @ApiResponse(responseCode = "404", description = "Suscripción no encontrada")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PutMapping("/{id}")
    public ResponseEntity<SuscripcionResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody SuscripcionUpdateDTO dto) {
        log.info("PUT /api/suscripciones/{}", id);
        return ResponseEntity.ok(suscripcionService.update(id, dto));
    }

    @Operation(summary = "Desactivar suscripción", description = "Desactiva una suscripción por su ID (eliminación lógica)")
    @ApiResponse(responseCode = "204", description = "Suscripción desactivada")
    @ApiResponse(responseCode = "404", description = "Suscripción no encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/suscripciones/{}", id);
        suscripcionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
