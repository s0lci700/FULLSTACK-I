package estacionamientos.ms_vehiculos.controller;

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

import jakarta.validation.Valid;

import estacionamientos.ms_vehiculos.dto.VehiculoCreateDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoResponseDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoUpdateDTO;
import estacionamientos.ms_vehiculos.service.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Vehículos", description = "Registro y gestión de vehículos del estacionamiento")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @Operation(summary = "Validar vehículo", description = "Verifica si existe un vehículo con la patente indicada")
    @ApiResponse(responseCode = "200", description = "true si existe, false si no")
    @GetMapping("/validar/{patente}")
    public ResponseEntity<Boolean> validar(@PathVariable String patente) {
        log.info("GET /api/vehiculos/validar/{}", patente);
        return ResponseEntity.ok(vehiculoService.exists(patente));
    }

    @Operation(summary = "Listar vehículos", description = "Retorna todos los vehículos registrados")
    @ApiResponse(responseCode = "200", description = "Listado de vehículos")
    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> listar() {
        log.info("GET /api/vehiculos");
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }

    @Operation(summary = "Obtener vehículo", description = "Busca un vehículo por su ID")
    @ApiResponse(responseCode = "200", description = "Vehículo encontrado")
    @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/vehiculos/{}", id);
        return ResponseEntity.ok(vehiculoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear vehículo", description = "Registra un nuevo vehículo. La patente debe ser única.")
    @ApiResponse(responseCode = "201", description = "Vehículo creado correctamente")
    @ApiResponse(responseCode = "409", description = "Patente ya registrada")
    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> crear(@Valid @RequestBody VehiculoCreateDTO dto) {
        log.info("POST /api/vehiculos");
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.crear(dto));
    }

    @Operation(summary = "Actualizar vehículo", description = "Actualiza los datos del vehículo (no modifica la patente)")
    @ApiResponse(responseCode = "200", description = "Vehículo actualizado")
    @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody VehiculoUpdateDTO dto) {
        log.info("PUT /api/vehiculos/{}", id);
        return ResponseEntity.ok(vehiculoService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar vehículo", description = "Eliminación lógica: marca activo=false")
    @ApiResponse(responseCode = "204", description = "Vehículo eliminado")
    @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/vehiculos/{}", id);
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vehículos por cliente", description = "Retorna todos los vehículos registrados de un cliente")
    @ApiResponse(responseCode = "200", description = "Listado de vehículos del cliente")
    @GetMapping("/cliente/{idClienteRef}")
    public ResponseEntity<List<VehiculoResponseDTO>> listarPorCliente(@PathVariable Long idClienteRef) {
        log.info("GET /api/vehiculos/cliente/{}", idClienteRef);
        return ResponseEntity.ok(vehiculoService.listarPorCliente(idClienteRef));
    }


}
