package estacionamientos.user_service.controller;

import estacionamientos.user_service.dto.ClienteCreateDTO;
import estacionamientos.user_service.dto.ClienteResponseDTO;
import estacionamientos.user_service.dto.ClienteSuscripcionCreateDTO;
import estacionamientos.user_service.dto.ClienteSuscripcionResponseDTO;
import estacionamientos.user_service.dto.ClienteUpdateDTO;
import estacionamientos.user_service.service.ClienteService;
import estacionamientos.user_service.service.ClienteSuscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes y sus suscripciones")
public class ClienteController {



    @Autowired
    ClienteService clienteService;
    
    @Autowired
    ClienteSuscripcionService clienteSuscripcionService;


    @Operation(summary = "Listar clientes", description = "Retorna todos los clientes registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Listado de clientes")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> getAll() {
        log.info("GET /api/clientes");
        return ResponseEntity.ok(clienteService.findAll());
    }

    @Operation(summary = "Obtener cliente", description = "Busca un cliente por su ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/clientes/{}", id);
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @Operation(summary = "Crear cliente", description = "Registra un nuevo cliente. El email debe ser único.")
    @ApiResponse(responseCode = "201", description = "Cliente creado correctamente")
    @ApiResponse(responseCode = "409", description = "Email ya registrado")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteCreateDTO dto) {
        log.info("POST /api/clientes");
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(dto));
    }

    @Operation(summary = "Actualizar cliente", description = "Actualiza nombre, apellido, teléfono y tipo de cliente")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDTO dto) {
        log.info("PUT /api/clientes/{}", id);
        return ResponseEntity.ok(clienteService.update(id, dto));
    }

    @Operation(summary = "Eliminar cliente", description = "Eliminación lógica: marca activo=false")
    @ApiResponse(responseCode = "204", description = "Cliente eliminado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{}", id);
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar suscripciones del cliente", description = "Retorna todas las suscripciones activas de un cliente")
    @ApiResponse(responseCode = "200", description = "Lista de suscripciones")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @GetMapping("/{id}/suscripciones")
    public ResponseEntity<List<ClienteSuscripcionResponseDTO>> getSuscripciones(@PathVariable Long id) {
        log.info("GET /api/clientes/{}/suscripciones", id);
        return ResponseEntity.ok(clienteSuscripcionService.findByClienteId(id));
    }

    @Operation(summary = "Asignar suscripción", description = "Asigna una suscripción a un cliente específico")
    @ApiResponse(responseCode = "201", description = "Suscripción asignada correctamente")
    @ApiResponse(responseCode = "404", description = "Cliente o suscripción no encontrada")
    @PostMapping("/{id}/suscripciones")
    public ResponseEntity<ClienteSuscripcionResponseDTO> addSuscripcion(
            @PathVariable Long id,
            @Valid @RequestBody ClienteSuscripcionCreateDTO dto) {
        log.info("POST /api/clientes/{}/suscripciones", id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteSuscripcionService.create(id, dto));
    }
}