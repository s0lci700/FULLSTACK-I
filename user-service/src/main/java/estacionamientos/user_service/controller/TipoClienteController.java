package estacionamientos.user_service.controller;

import estacionamientos.user_service.dto.TipoClienteCreateDTO;
import estacionamientos.user_service.dto.TipoClienteResponseDTO;
import estacionamientos.user_service.dto.TipoClienteUpdateDTO;
import estacionamientos.user_service.service.TipoClienteService;
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
@RequestMapping("/api/tipo-cliente")
@Tag(name = "Tipos de Cliente", description = "Catálogo de tipos de cliente (REGULAR, VIP, etc.)")
public class TipoClienteController {


    private final TipoClienteService tipoClienteService;

    public TipoClienteController(TipoClienteService tipoClienteService) {
        this.tipoClienteService = tipoClienteService;
    }

    @Operation(summary = "Listar tipos de cliente", description = "Retorna todos los tipos de cliente disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de tipos de cliente")
    @GetMapping
    public ResponseEntity<List<TipoClienteResponseDTO>> getAll() {
        log.info("GET /api/tipo-cliente");
        return ResponseEntity.ok(tipoClienteService.findAll());
    }

    @Operation(summary = "Obtener tipo de cliente", description = "Busca un tipo de cliente por su ID")
    @ApiResponse(responseCode = "200", description = "Tipo de cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Tipo de cliente no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<TipoClienteResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tipo-cliente/{}", id);
        return ResponseEntity.ok(tipoClienteService.findById(id));
    }

    @Operation(summary = "Crear tipo de cliente", description = "Registra un nuevo tipo de cliente")
    @ApiResponse(responseCode = "201", description = "Tipo creado correctamente")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PostMapping
    public ResponseEntity<TipoClienteResponseDTO> create(@Valid @RequestBody TipoClienteCreateDTO dto) {
        log.info("POST /api/tipo-cliente");
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoClienteService.create(dto));
    }

    @Operation(summary = "Actualizar tipo de cliente", description = "Actualiza los datos de un tipo existente")
    @ApiResponse(responseCode = "200", description = "Tipo actualizado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PutMapping("/{id}")
    public ResponseEntity<TipoClienteResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody TipoClienteUpdateDTO dto) {
        log.info("PUT /api/tipo-cliente/{}", id);
        return ResponseEntity.ok(tipoClienteService.update(id, dto));
    }

    @Operation(summary = "Eliminar tipo de cliente", description = "Elimina un tipo de cliente por su ID")
    @ApiResponse(responseCode = "204", description = "Tipo eliminado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tipo-cliente/{}", id);
        tipoClienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}