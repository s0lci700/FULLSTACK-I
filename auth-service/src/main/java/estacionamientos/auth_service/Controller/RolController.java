package estacionamientos.auth_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import estacionamientos.auth_service.dto.RolCreateDTO;
import estacionamientos.auth_service.dto.RolResponseDTO;
import estacionamientos.auth_service.dto.RolUpdateDTO;
import estacionamientos.auth_service.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Catálogo de roles de usuario (ADMIN, CLIENTE, etc.)")
public class RolController {

    @Autowired
    RolService rolService;

    @Operation(summary = "Listar roles", description = "Retorna el catálogo completo de roles")
    @ApiResponse(responseCode = "200", description = "Listado de roles")
    @GetMapping
    public ResponseEntity<List<RolResponseDTO>> listar() {
        log.info("GET /api/roles");
        return ResponseEntity.ok(rolService.findAll());
    }

    @Operation(summary = "Obtener rol", description = "Busca un rol por su ID")
    @ApiResponse(responseCode = "200", description = "Rol encontrado")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<RolResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/roles/{}", id);
        return ResponseEntity.ok(rolService.findById(id));
    }

    @Operation(summary = "Crear rol", description = "Registra un nuevo rol")
    @ApiResponse(responseCode = "201", description = "Rol creado correctamente")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PostMapping
    public ResponseEntity<RolResponseDTO> crear(@Valid @RequestBody RolCreateDTO dto) {
        log.info("POST /api/roles");
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.create(dto));
    }

    @Operation(summary = "Actualizar rol", description = "Actualiza los datos de un rol existente")
    @ApiResponse(responseCode = "200", description = "Rol actualizado")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PutMapping("/{id}")
    public ResponseEntity<RolResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody RolUpdateDTO dto) {
        log.info("PUT /api/roles/{}", id);
        return ResponseEntity.ok(rolService.update(id, dto));
    }

    @Operation(summary = "Eliminar rol", description = "Elimina un rol por su ID")
    @ApiResponse(responseCode = "204", description = "Rol eliminado")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/roles/{}", id);
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
