package estacionamientos.user_service.controller;

import estacionamientos.user_service.dto.SuscripcionResponseDTO;
import estacionamientos.user_service.service.SuscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
