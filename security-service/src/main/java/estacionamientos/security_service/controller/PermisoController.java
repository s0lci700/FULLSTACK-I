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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/permisos")
@Tag(name = "Permisos", description = "Gestión de permisos del sistema de seguridad")
public class PermisoController {



    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @Operation(summary = "Listar permisos", description = "Retorna todos los permisos registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Listado de permisos")
    @GetMapping
    public ResponseEntity<List<PermisoResponseDTO>> getAll() {
        log.info("GET /api/permisos");
        return ResponseEntity.ok(permisoService.findAll());
    }

    @Operation(summary = "Obtener permiso", description = "Busca un permiso por su ID")
    @ApiResponse(responseCode = "200", description = "Permiso encontrado")
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<PermisoResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/permisos/{}", id);
        return ResponseEntity.ok(permisoService.findById(id));
    }

    @Operation(summary = "Crear permiso", description = "Crea un nuevo permiso. El nombre debe ser único.")
    @ApiResponse(responseCode = "201", description = "Permiso creado correctamente")
    @ApiResponse(responseCode = "409", description = "Nombre de permiso ya existe")
    @PostMapping
    public ResponseEntity<PermisoResponseDTO> create(@Valid @RequestBody PermisoCreateDTO dto) {
        log.info("POST /api/permisos");
        return ResponseEntity.status(HttpStatus.CREATED).body(permisoService.create(dto));
    }

    @Operation(summary = "Actualizar permiso", description = "Actualiza nombre y descripción de un permiso existente")
    @ApiResponse(responseCode = "200", description = "Permiso actualizado")
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<PermisoResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody PermisoCreateDTO dto) {
        log.info("PUT /api/permisos/{}", id);
        return ResponseEntity.ok(permisoService.update(id, dto));
    }

    @Operation(summary = "Eliminar permiso", description = "Elimina un permiso por su ID")
    @ApiResponse(responseCode = "204", description = "Permiso eliminado")
    @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/permisos/{}", id);
        permisoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}