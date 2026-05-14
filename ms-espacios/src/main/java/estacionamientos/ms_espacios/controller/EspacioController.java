package estacionamientos.ms_espacios.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_espacios.dto.EspacioCreateDTO;
import estacionamientos.ms_espacios.dto.EspacioResponseDTO;
import estacionamientos.ms_espacios.dto.EspacioUpdateDTO;
import estacionamientos.ms_espacios.service.EspacioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/espacios")
public class EspacioController {

    private final EspacioService espaciosService;

    public EspacioController(EspacioService espaciosService) {
        this.espaciosService = espaciosService;
    }
    // Retorna la lista completa de todos los espacios registrados
    @GetMapping
    public ResponseEntity<List<EspacioResponseDTO>> getAll() {
        log.info("GET /api/espacios");
        return ResponseEntity.ok(espaciosService.findAll());
    }
    // Busca un espacio por su ID, lanza 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<EspacioResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/espacios/{}", id);
        return ResponseEntity.ok(espaciosService.findById(id));
    }
    // Retorna solo los espacios que tienen disponible=true
    // Lo usa ms-reservas para saber dónde puede hacer una reserva
    @GetMapping("/disponibles")
    public ResponseEntity<List<EspacioResponseDTO>> getDisponibles() {
        log.info("GET /api/espacios/disponibles");
        return ResponseEntity.ok(espaciosService.findDisponibles());
    }
    // Crea un nuevo espacio, valida que el numero no esté duplicado
    // Retorna 201 CREATED con el espacio creado
    @PostMapping
    public ResponseEntity<EspacioResponseDTO> create(@Valid @RequestBody EspacioCreateDTO dto) {
        log.info("POST /api/espacios");
        return ResponseEntity.status(HttpStatus.CREATED).body(espaciosService.create(dto));
    }
     // Actualiza los datos de un espacio existente por ID
    // No cambia la disponibilidad, eso se hace con el PATCH
    @PutMapping("/{id}")
    public ResponseEntity<EspacioResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody EspacioUpdateDTO dto) {
        log.info("PUT /api/espacios/{}", id);
        return ResponseEntity.ok(espaciosService.update(id, dto));
    }
    // Cambia solo la disponibilidad del espacio (true/false)
    // Lo llama ms-accesos cuando registra entrada (false) o salida (true)
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<Map<String, String>> updateDisponibilidad(@PathVariable Long id,
                                                                     @RequestParam Boolean disponible) {
        log.info("PATCH /api/espacios/{}/disponibilidad -> {}", id, disponible);
        espaciosService.updateDisponibilidad(id, disponible);
        return ResponseEntity.ok(Map.of("mensaje", "Disponibilidad actualizada correctamente"));
    }
    // Elimina un espacio por ID, retorna 204 sin contenido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/espacios/{}", id);
        espaciosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}