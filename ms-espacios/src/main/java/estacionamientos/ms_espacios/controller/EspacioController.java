package estacionamientos.ms_espacios.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_espacios.dto.EspacioCreateDTO;
import estacionamientos.ms_espacios.dto.EspacioResponseDTO;
import estacionamientos.ms_espacios.dto.EspacioUpdateDTO;
import estacionamientos.ms_espacios.service.EspacioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/espacios")
@Tag(name = "Espacios", description = "Gestión de espacios de estacionamiento y disponibilidad")
public class EspacioController {

    @Autowired
    private EspacioService espaciosService;

    @Operation(summary = "Listar espacios", description = "Retorna todos los espacios del estacionamiento")
    @ApiResponse(responseCode = "200", description = "Listado de espacios")
    @GetMapping
    public ResponseEntity<List<EspacioResponseDTO>> getAll() {
        log.info("GET /api/espacios");
        return ResponseEntity.ok(espaciosService.findAll());
    }
    @Operation(summary = "Obtener espacio", description = "Busca un espacio por su ID")
    @ApiResponse(responseCode = "200", description = "Espacio encontrado")
    @ApiResponse(responseCode = "404", description = "Espacio no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EspacioResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/espacios/{}", id);
        return ResponseEntity.ok(espaciosService.findById(id));
    }
    @Operation(summary = "Espacios por zona", description = "Retorna espacios filtrados por zona")
    @ApiResponse(responseCode = "200", description = "Espacios de la zona")
    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<EspacioResponseDTO>> getByZona(@PathVariable String zona) {
        log.info("GET /api/espacios/zona/{}", zona);
        return ResponseEntity.ok(espaciosService.findByZona(zona));
    }
    @Operation(summary = "Espacios disponibles", description = "Retorna solo los espacios con disponible=true. Consumido por ms-reservas.")
    @ApiResponse(responseCode = "200", description = "Espacios disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<List<EspacioResponseDTO>> getDisponibles() {
        log.info("GET /api/espacios/disponibles");
        return ResponseEntity.ok(espaciosService.findDisponibles());
    }
    @Operation(summary = "Crear espacio", description = "Registra un nuevo espacio de estacionamiento")
    @ApiResponse(responseCode = "201", description = "Espacio creado correctamente")
    @ApiResponse(responseCode = "409", description = "Número de espacio duplicado")
    @PostMapping
    public ResponseEntity<EspacioResponseDTO> create(@Valid @RequestBody EspacioCreateDTO dto) {
        log.info("POST /api/espacios");
        return ResponseEntity.status(HttpStatus.CREATED).body(espaciosService.create(dto));
    }
    @Operation(summary = "Actualizar espacio", description = "Actualiza los datos del espacio (no modifica disponibilidad)")
    @ApiResponse(responseCode = "200", description = "Espacio actualizado")
    @ApiResponse(responseCode = "404", description = "Espacio no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<EspacioResponseDTO> update(@PathVariable Long id,
    @Valid @RequestBody EspacioUpdateDTO dto) {
        log.info("PUT /api/espacios/{}", id);
        return ResponseEntity.ok(espaciosService.update(id, dto));
    }
    @Operation(summary = "Cambiar disponibilidad", description = "Actualiza disponible=true/false. Llamado por ms-accesos en entrada/salida.")
    @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada")
    @ApiResponse(responseCode = "404", description = "Espacio no encontrado")
    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Map<String, String>> updateDisponibilidad(@PathVariable Long id,
                                                                     @RequestParam Boolean disponible) {
        log.info("PATCH /api/espacios/{}/disponibilidad -> {}", id, disponible);
        espaciosService.updateDisponibilidad(id, disponible);
        return ResponseEntity.ok(Map.of("mensaje", "Disponibilidad actualizada correctamente"));
    }
    @Operation(summary = "Eliminar espacio", description = "Elimina un espacio por su ID")
    @ApiResponse(responseCode = "204", description = "Espacio eliminado")
    @ApiResponse(responseCode = "404", description = "Espacio no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/espacios/{}", id);
        espaciosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}