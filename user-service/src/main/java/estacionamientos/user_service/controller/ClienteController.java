package estacionamientos.user_service.controller;

import estacionamientos.user_service.dto.ClienteCreateDTO;
import estacionamientos.user_service.dto.ClienteResponseDTO;
import estacionamientos.user_service.dto.ClienteSuscripcionCreateDTO;
import estacionamientos.user_service.dto.ClienteSuscripcionResponseDTO;
import estacionamientos.user_service.dto.ClienteUpdateDTO;
import estacionamientos.user_service.service.ClienteService;
import estacionamientos.user_service.service.ClienteSuscripcionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;
    private final ClienteSuscripcionService clienteSuscripcionService;

    public ClienteController(ClienteService clienteService,
                             ClienteSuscripcionService clienteSuscripcionService) {
        this.clienteService = clienteService;
        this.clienteSuscripcionService = clienteSuscripcionService;
    }

    // Retorna todos los clientes registrados
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> getAll() {
        log.info("GET /api/clientes");
        return ResponseEntity.ok(clienteService.findAll());
    }

    // Busca un cliente por id, retorna 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/clientes/{}", id);
        return ResponseEntity.ok(clienteService.findById(id));
    }

    // Crea un nuevo cliente, valida email unico
    // Retorna 201 CREATED con el cliente creado
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteCreateDTO dto) {
        log.info("POST /api/clientes");
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(dto));
    }

    // Actualiza nombre, apellido, telefono y tipo de cliente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody ClienteUpdateDTO dto) {
        log.info("PUT /api/clientes/{}", id);
        return ResponseEntity.ok(clienteService.update(id, dto));
    }

    // Eliminacion logica — marca activo=false, retorna 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{}", id);
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Retorna todas las suscripciones de un cliente
    @GetMapping("/{id}/suscripciones")
    public ResponseEntity<List<ClienteSuscripcionResponseDTO>> getSuscripciones(@PathVariable Long id) {
        log.info("GET /api/clientes/{}/suscripciones", id);
        return ResponseEntity.ok(clienteSuscripcionService.findByClienteId(id));
    }

    // Asigna una suscripcion a un cliente especifico
    // Retorna 201 CREATED con la suscripcion asignada
    @PostMapping("/{id}/suscripciones")
    public ResponseEntity<ClienteSuscripcionResponseDTO> addSuscripcion(
            @PathVariable Long id,
            @Valid @RequestBody ClienteSuscripcionCreateDTO dto) {
        log.info("POST /api/clientes/{}/suscripciones", id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteSuscripcionService.create(id, dto));
    }
}