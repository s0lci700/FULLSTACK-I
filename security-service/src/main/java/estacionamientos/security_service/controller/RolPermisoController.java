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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/roles-permisos")
public class RolPermisoController {



    private final RolPermisoService rolPermisoService;

    public RolPermisoController(RolPermisoService rolPermisoService) {
        this.rolPermisoService = rolPermisoService;
    }

    // Retorna todas las asignaciones rol-permiso registradas
    @GetMapping
    public ResponseEntity<List<RolPermisoResponseDTO>> getAll() {
        log.info("GET /api/roles-permisos");
        return ResponseEntity.ok(rolPermisoService.findAll());
    }

    // Retorna todos los permisos asignados a un rol especifico
    // Util para saber que acciones puede realizar un rol determinado
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<List<RolPermisoResponseDTO>> getByRol(@PathVariable Long idRol) {
        log.info("GET /api/roles-permisos/rol/{}", idRol);
        return ResponseEntity.ok(rolPermisoService.findByIdRol(idRol));
    }

    // Asigna un permiso a un rol
    // Valida que el permiso exista y que no este ya asignado a ese rol
    // Retorna 201 CREATED con la asignacion creada
    @PostMapping
    public ResponseEntity<RolPermisoResponseDTO> create(@Valid @RequestBody RolPermisoCreateDTO dto) {
        log.info("POST /api/roles-permisos");
        return ResponseEntity.status(HttpStatus.CREATED).body(rolPermisoService.create(dto));
    }

    // Elimina una asignacion rol-permiso por su ID
    // Retorna 204 sin contenido si se elimino correctamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/roles-permisos/{}", id);
        rolPermisoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}