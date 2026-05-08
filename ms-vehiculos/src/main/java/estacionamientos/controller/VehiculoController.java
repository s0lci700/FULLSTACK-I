package estacionamientos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.service.VehiculoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
// TODO: Cambiar a "/api/vehiculos" (plural) — convención REST para colecciones.
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/validar/{patente}")
    public ResponseEntity<Boolean> validar(
            @PathVariable String patente) {

        return ResponseEntity.ok(vehiculoService.exists(patente));
    }

    // TODO: Agregar los endpoints CRUD completos:
    // GET    /api/vehiculos              → listar todos
    // GET    /api/vehiculos/{id}         → obtener por id
    // POST   /api/vehiculos              → crear (recibe VehiculoCreateDTO)
    // PUT    /api/vehiculos/{id}         → actualizar (recibe VehiculoUpdateDTO)
    // DELETE /api/vehiculos/{id}         → eliminar o desactivar
    // GET    /api/vehiculos/cliente/{id} → listar por idClienteRef
    // GET    /api/tipo-vehiculo          → listar todos los tipos (controlador separado)

}

