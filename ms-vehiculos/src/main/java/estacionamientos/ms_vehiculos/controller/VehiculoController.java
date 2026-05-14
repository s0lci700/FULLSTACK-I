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

import estacionamientos.ms_vehiculos.dto.VehiculoCreateDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoResponseDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoUpdateDTO;
import estacionamientos.ms_vehiculos.service.VehiculoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/validar/{patente}")
    public ResponseEntity<Boolean> validar(@PathVariable String patente) {
        log.info("GET /api/vehiculos/validar/{}", patente);
        return ResponseEntity.ok(vehiculoService.exists(patente));
    }

    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> listar() {
        log.info("GET /api/vehiculos");
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/vehiculos/{}", id);
        return ResponseEntity.ok(vehiculoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> crear(@RequestBody VehiculoCreateDTO dto) {
        log.info("POST /api/vehiculos");
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> actualizar(@PathVariable Long id, @RequestBody VehiculoUpdateDTO dto) {
        log.info("PUT /api/vehiculos/{}", id);
        return ResponseEntity.ok(vehiculoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/vehiculos/{}", id);
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{idClienteRef}")
    public ResponseEntity<List<VehiculoResponseDTO>> listarPorCliente(@PathVariable Long idClienteRef) {
        log.info("GET /api/vehiculos/cliente/{}", idClienteRef);
        return ResponseEntity.ok(vehiculoService.listarPorCliente(idClienteRef));
    }


}
