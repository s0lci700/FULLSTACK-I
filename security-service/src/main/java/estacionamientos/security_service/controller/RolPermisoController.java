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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.security_service.dto.RolPermisoCreateDTO;
import estacionamientos.security_service.dto.RolPermisoResponseDTO;
import estacionamientos.security_service.service.RolPermisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/roles-permisos")
@Tag(name = "Roles y Permisos", description = "Asignación de permisos a roles del sistema")
public class RolPermisoController {



    private final RolPermisoService rolPermisoService;

    public RolPermisoController(RolPermisoService rolPermisoService) {
        this.rolPermisoService = rolPermisoService;
    }

    @Operation(summary = "Listar asignaciones", description = "Retorna todas las asignaciones rol-permiso registradas")
    @ApiResponse(responseCode = "200", description = "Listado de asignaciones")
    @GetMapping
    public ResponseEntity<List<RolPermisoResponseDTO>> getAll() {
        log.info("GET /api/roles-permisos");
        return ResponseEntity.ok(rolPermisoService.findAll());
    }

    @Operation(summary = "Permisos por rol", description = "Retorna todos los permisos asignados a un rol específico")
    @ApiResponse(responseCode = "200", description = "Permisos del rol")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<List<RolPermisoResponseDTO>> getByRol(@PathVariable Long idRol) {
        log.info("GET /api/roles-permisos/rol/{}", idRol);
        return ResponseEntity.ok(rolPermisoService.findByIdRol(idRol));
    }

    @Operation(summary = "Asignar permiso a rol", description = "Asigna un permiso a un rol. Valida que no esté duplicado.")
    @ApiResponse(responseCode = "201", description = "Asignación creada correctamente")
    @ApiResponse(responseCode = "409", description = "Permiso ya asignado a ese rol")
    @PostMapping
    public ResponseEntity<RolPermisoResponseDTO> create(@Valid @RequestBody RolPermisoCreateDTO dto) {
        log.info("POST /api/roles-permisos");
        return ResponseEntity.status(HttpStatus.CREATED).body(rolPermisoService.create(dto));
    }

    @Operation(summary = "Eliminar asignación", description = "Elimina una asignación rol-permiso por su ID")
    @ApiResponse(responseCode = "204", description = "Asignación eliminada")
    @ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/roles-permisos/{}", id);
        rolPermisoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}