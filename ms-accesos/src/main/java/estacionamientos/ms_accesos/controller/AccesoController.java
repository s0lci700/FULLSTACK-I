package estacionamientos.ms_accesos.controller;

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

import java.util.List;

import estacionamientos.ms_accesos.dto.AccesoCreateDTO;
import estacionamientos.ms_accesos.dto.AccesoResponseDTO;
import estacionamientos.ms_accesos.service.AccesoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/accesos")
@Tag(name = "Accesos", description = "Registro de entradas y salidas del estacionamiento")
public class AccesoController {

    @Autowired
    private AccesoService accesoService;

    @Operation(summary = "Listar accesos", description = "Retorna todos los registros de acceso")
    @ApiResponse(responseCode = "200", description = "Listado de accesos")
    @GetMapping
    public ResponseEntity<List<AccesoResponseDTO>> getAll() {
        log.info("GET /api/accesos");
        return ResponseEntity.ok(accesoService.findAll());
    }

    @Operation(summary = "Registrar entrada", description = "Crea un acceso en estado ACTIVO y marca el espacio como no disponible")
    @ApiResponse(responseCode = "201", description = "Entrada registrada correctamente")
    @ApiResponse(responseCode = "400", description = "Espacio no disponible o reserva inválida")
    @PostMapping("/entrada")
    public ResponseEntity<AccesoResponseDTO> registrarEntrada(
        @Valid @RequestBody AccesoCreateDTO dto) {
            log.info("POST /api/accesos/entrada - idReserva={}", dto.getIdReserva());
            return ResponseEntity.status(HttpStatus.CREATED).body(accesoService.registrarEntrada(dto));
    }

    @Operation(summary = "Registrar salida", description = "Cierra el acceso, calcula minutos, estado=COMPLETADO, libera espacio")
    @ApiResponse(responseCode = "200", description = "Salida registrada correctamente")
    @ApiResponse(responseCode = "404", description = "Acceso no encontrado")
    @ApiResponse(responseCode = "422", description = "Acceso ya cerrado")
    @PutMapping("/{id}/salida")
    public ResponseEntity<AccesoResponseDTO> registrarSalida(
        @PathVariable Long id) {
        log.info("PUT /api/accesos/{}/salida", id);
        return ResponseEntity.ok(accesoService.registrarSalida(id));
    }

    @Operation(summary = "Obtener acceso", description = "Busca un acceso por su ID")
    @ApiResponse(responseCode = "200", description = "Acceso encontrado")
    @ApiResponse(responseCode = "404", description = "Acceso no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<AccesoResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/accesos/{}", id);
        return ResponseEntity.ok(accesoService.findById(id));
    }

    @Operation(summary = "Acceso por reserva", description = "Retorna el acceso asociado a una reserva específica")
    @ApiResponse(responseCode = "200", description = "Acceso encontrado")
    @ApiResponse(responseCode = "404", description = "No existe acceso para esa reserva")
    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<AccesoResponseDTO> getByReserva(
        @PathVariable Long idReserva) {
        log.info("GET /api/accesos/reserva/{}", idReserva);
        return ResponseEntity.ok(accesoService.findByReserva(idReserva));
    }

}
