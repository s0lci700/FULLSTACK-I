package estacionamientos.ms_reservas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_reservas.dto.ReservaCreateDTO;
import estacionamientos.ms_reservas.dto.ReservaResponseDTO;
import estacionamientos.ms_reservas.model.EstadoEnums;
import estacionamientos.ms_reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas de espacios de estacionamiento")
public class ReservaController {

    @Autowired
    private ReservaService reservasService;

    @Operation(summary = "Listar reservas", description = "Retorna todas las reservas registradas")
    @ApiResponse(responseCode = "200", description = "Listado de reservas")
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> findAll() {
        log.info("GET /api/reservas");
        return ResponseEntity.ok(reservasService.findAll());
    }

    @Operation(summary = "Obtener reserva", description = "Busca una reserva por su ID")
    @ApiResponse(responseCode = "200", description = "Reserva encontrada")
    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> findById(@PathVariable Long id) {
        log.info("GET /api/reservas/{}", id);
        return ResponseEntity.ok(reservasService.findById(id));
    }

    @Operation(summary = "Reservas por cliente", description = "Retorna todas las reservas de un cliente")
    @ApiResponse(responseCode = "200", description = "Reservas del cliente")
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<ReservaResponseDTO>> findByIdCliente(@PathVariable Long id) {
        log.info("GET /api/reservas/cliente/{}", id);
        return ResponseEntity.ok(reservasService.findByIdCliente(id));
    }

    @Operation(summary = "Reservas por estado", description = "Filtra reservas por estado: PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA")
    @ApiResponse(responseCode = "200", description = "Reservas filtradas por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDTO>> findByEstado(@PathVariable EstadoEnums estado) {
        log.info("GET /api/reservas/estado/{}", estado);
        return ResponseEntity.ok(reservasService.findByEstado(estado));
    }

    @Operation(summary = "Crear reserva", description = "Crea una reserva en estado PENDIENTE. Valida cliente, vehículo y espacio via Feign.")
    @ApiResponse(responseCode = "201", description = "Reserva creada correctamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o cliente/vehículo/espacio inactivo")
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> create(
        @Valid @RequestBody ReservaCreateDTO reserva) {
        log.info("POST /api/reservas");
        return ResponseEntity.status(HttpStatus.CREATED).body(reservasService.create(reserva));
    }

    @Operation(summary = "Cancelar reserva", description = "Cambia el estado de PENDIENTE a CANCELADA")
    @ApiResponse(responseCode = "200", description = "Reserva cancelada")
    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        log.info("PUT /api/reservas/{}/cancelar", id);
        return ResponseEntity.ok(reservasService.cancelar(id));
    }

    @Operation(summary = "Confirmar reserva", description = "Cambia el estado de PENDIENTE a CONFIRMADA")
    @ApiResponse(responseCode = "200", description = "Reserva confirmada")
    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponseDTO> confirmar(@PathVariable Long id) {
        log.info("PUT /api/reservas/{}/confirmar", id);
        return ResponseEntity.ok(reservasService.confirmar(id));
    }

    @Operation(summary = "Finalizar reserva", description = "Cambia el estado de CONFIRMADA a FINALIZADA")
    @ApiResponse(responseCode = "200", description = "Reserva finalizada")
    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ReservaResponseDTO> finalizar(@PathVariable Long id) {
        log.info("PUT /api/reservas/{}/finalizar", id);
        return ResponseEntity.ok(reservasService.finalizar(id));
    }
}
