package estacionamientos.ms_tarifas.controller;

import estacionamientos.ms_tarifas.dto.TarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.TarifaResponseDTO;
import estacionamientos.ms_tarifas.dto.TarifaUpdateDTO;
import estacionamientos.ms_tarifas.service.TarifasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tarifas")
@Tag(name = "Tarifas", description = "Gestión de tarifas base del estacionamiento")
public class TarifasController {

    private final TarifasService tarifasService;

    public TarifasController(TarifasService tarifasService) {
        this.tarifasService = tarifasService;
    }

    @Operation(summary = "Listar tarifas", description = "Retorna todas las tarifas registradas")
    @ApiResponse(responseCode = "200", description = "Listado de tarifas")
    @GetMapping
    public ResponseEntity<List<TarifaResponseDTO>> getAll() {
        log.info("GET /api/tarifas");
        return ResponseEntity.ok(tarifasService.findAll());
    }

    @Operation(summary = "Obtener tarifa", description = "Busca una tarifa por su ID")
    @ApiResponse(responseCode = "200", description = "Tarifa encontrada")
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<TarifaResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tarifas/{}", id);
        return ResponseEntity.ok(tarifasService.findById(id));
    }

    @Operation(summary = "Tarifa vigente", description = "Retorna la tarifa activa actualmente. Consumido por ms-pagos via Feign.")
    @ApiResponse(responseCode = "200", description = "Tarifa vigente")
    @GetMapping("/vigente")
    public ResponseEntity<TarifaResponseDTO> getVigente() {
        log.info("GET /api/tarifas/vigente");
        return ResponseEntity.ok(tarifasService.findVigente());
    }

    @Operation(summary = "Crear tarifa", description = "Registra una nueva tarifa. El nombre debe ser único.")
    @ApiResponse(responseCode = "201", description = "Tarifa creada correctamente")
    @ApiResponse(responseCode = "409", description = "Nombre de tarifa duplicado")
    @PostMapping
    public ResponseEntity<TarifaResponseDTO> create(@Valid @RequestBody TarifaCreateDTO dto) {
        log.info("POST /api/tarifas");
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifasService.create(dto));
    }

    @Operation(summary = "Actualizar tarifa", description = "Actualiza los datos de una tarifa existente")
    @ApiResponse(responseCode = "200", description = "Tarifa actualizada")
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<TarifaResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody TarifaUpdateDTO dto) {
        log.info("PUT /api/tarifas/{}", id);
        return ResponseEntity.ok(tarifasService.update(id, dto));
    }

    @Operation(summary = "Eliminar tarifa", description = "Eliminación lógica: marca activo=false")
    @ApiResponse(responseCode = "204", description = "Tarifa eliminada")
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tarifas/{}", id);
        tarifasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
