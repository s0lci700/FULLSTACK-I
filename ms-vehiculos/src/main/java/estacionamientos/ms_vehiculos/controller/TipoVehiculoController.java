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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/tipos-vehiculo")
public class TipoVehiculoController {

    @Autowired
    TipoVehiculoService tipoVehiculoService;

    @GetMapping
    public ResponseEntity<List<TipoVehiculo>> listar() {
        log.info("GET /api/tipos-vehiculo");
        return ResponseEntity.ok(tipoVehiculoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoVehiculo> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/tipos-vehiculo/{}", id);
        return ResponseEntity.ok(tipoVehiculoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoVehiculo> crear(@Valid @RequestBody TipoVehiculoCreateDTO dto) {
        log.info("POST /api/tipos-vehiculo");
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoVehiculoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoVehiculo> actualizar(@PathVariable Long id,
            @Valid @RequestBody TipoVehiculoUpdateDTO dto) {
        log.info("PUT /api/tipos-vehiculo/{}", id);
        return ResponseEntity.ok(tipoVehiculoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/tipos-vehiculo/{}", id);
        tipoVehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
