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

import estacionamientos.ms_vehiculos.dto.TipoVehiculoCreateDTO;
import estacionamientos.ms_vehiculos.dto.TipoVehiculoUpdateDTO;
import estacionamientos.ms_vehiculos.model.TipoVehiculo;
import estacionamientos.ms_vehiculos.service.TipoVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/tipos-vehiculo")
@Tag(name = "Tipos de Vehículo", description = "Catálogo de tipos de vehículo (AUTO, MOTO, CAMIONETA, etc.)")
public class TipoVehiculoController {

    @Autowired
    TipoVehiculoService tipoVehiculoService;

    @Operation(summary = "Listar tipos de vehículo", description = "Retorna el catálogo completo de tipos de vehículo")
    @ApiResponse(responseCode = "200", description = "Listado de tipos")
    @GetMapping
    public ResponseEntity<List<TipoVehiculo>> listar() {
        log.info("GET /api/tipos-vehiculo");
        return ResponseEntity.ok(tipoVehiculoService.listarTodos());
    }

    @Operation(summary = "Obtener tipo de vehículo", description = "Busca un tipo por su ID")
    @ApiResponse(responseCode = "200", description = "Tipo encontrado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<TipoVehiculo> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/tipos-vehiculo/{}", id);
        return ResponseEntity.ok(tipoVehiculoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear tipo de vehículo", description = "Registra un nuevo tipo de vehículo")
    @ApiResponse(responseCode = "201", description = "Tipo creado correctamente")
    @PostMapping
    public ResponseEntity<TipoVehiculo> crear(@Valid @RequestBody TipoVehiculoCreateDTO dto) {
        log.info("POST /api/tipos-vehiculo");
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoVehiculoService.crear(dto));
    }

    @Operation(summary = "Actualizar tipo de vehículo", description = "Actualiza los datos de un tipo existente")
    @ApiResponse(responseCode = "200", description = "Tipo actualizado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<TipoVehiculo> actualizar(@PathVariable Long id,
            @Valid @RequestBody TipoVehiculoUpdateDTO dto) {
        log.info("PUT /api/tipos-vehiculo/{}", id);
        return ResponseEntity.ok(tipoVehiculoService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar tipo de vehículo", description = "Elimina un tipo por su ID")
    @ApiResponse(responseCode = "204", description = "Tipo eliminado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/tipos-vehiculo/{}", id);
        tipoVehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
