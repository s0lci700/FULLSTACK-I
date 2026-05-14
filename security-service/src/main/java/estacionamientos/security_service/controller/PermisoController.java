package estacionamientos.security_service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import estacionamientos.security_service.dto.PermisoCreateDTO;
import estacionamientos.security_service.dto.PermisoResponseDTO;
import estacionamientos.security_service.service.PermisoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private static final Logger log = LoggerFactory.getLogger(PermisoController.class);

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    // Retorna la lista completa de permisos registrados en el sistema
    @GetMapping
    public ResponseEntity<List<PermisoResponseDTO>> getAll() {
        log.info("GET /api/permisos");
        return ResponseEntity.ok(permisoService.findAll());
    }

    // Busca un permiso por su ID
    // Retorna 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<PermisoResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/permisos/{}", id);
        return ResponseEntity.ok(permisoService.findById(id));
    }

    // Crea un nuevo permiso validando que el nombre no este duplicado
    // Retorna 201 CREATED con el permiso creado
    @PostMapping
    public ResponseEntity<PermisoResponseDTO> create(@Valid @RequestBody PermisoCreateDTO dto) {
        log.info("POST /api/permisos");
        return ResponseEntity.status(HttpStatus.CREATED).body(permisoService.create(dto));
    }

    // Actualiza nombre y descripcion de un permiso existente
    // Retorna 404 si el permiso no existe
    @PutMapping("/{id}")
    public ResponseEntity<PermisoResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody PermisoCreateDTO dto) {
        log.info("PUT /api/permisos/{}", id);
        return ResponseEntity.ok(permisoService.update(id, dto));
    }

    // Elimina un permiso por su ID
    // Retorna 204 sin contenido si se elimino correctamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/permisos/{}", id);
        permisoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}