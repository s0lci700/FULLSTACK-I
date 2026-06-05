package estacionamientos.ms_tarifas.controller;

import estacionamientos.ms_tarifas.dto.HorarioTarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.HorarioTarifaResponseDTO;
import estacionamientos.ms_tarifas.service.HorarioTarifasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/horarios-tarifa")
@Tag(name = "Horarios de Tarifa", description = "Multiplicadores por horario (LABORAL, FIN_DE_SEMANA, FESTIVO)")
public class HorarioTarifasController {

    private final HorarioTarifasService horarioTarifasService;

    public HorarioTarifasController(HorarioTarifasService horarioTarifasService) {
        this.horarioTarifasService = horarioTarifasService;
    }

    @Operation(summary = "Horario vigente", description = "Retorna el horario activo para el momento actual. Consumido por ms-pagos via Feign.")
    @ApiResponse(responseCode = "200", description = "Horario vigente")
    @GetMapping("/vigente")
    public ResponseEntity<HorarioTarifaResponseDTO> getVigente() {
        log.info("GET /api/horarios-tarifa/vigente");
        return ResponseEntity.ok(horarioTarifasService.findVigente());
    }

    @Operation(summary = "Listar horarios", description = "Retorna todos los horarios de tarifa registrados")
    @ApiResponse(responseCode = "200", description = "Listado de horarios")
    @GetMapping
    public ResponseEntity<List<HorarioTarifaResponseDTO>> getAll() {
        log.info("GET /api/horarios-tarifa");
        return ResponseEntity.ok(horarioTarifasService.findAll());
    }

    @Operation(summary = "Obtener horario", description = "Busca un horario de tarifa por su ID")
    @ApiResponse(responseCode = "200", description = "Horario encontrado")
    @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<HorarioTarifaResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/horarios-tarifa/{}", id);
        return ResponseEntity.ok(horarioTarifasService.findById(id));
    }

    @Operation(summary = "Crear horario", description = "Crea un nuevo horario asociado a una tarifa existente")
    @ApiResponse(responseCode = "201", description = "Horario creado correctamente")
    @ApiResponse(responseCode = "404", description = "Tarifa asociada no encontrada")
    @PostMapping
    public ResponseEntity<HorarioTarifaResponseDTO> create(@Valid @RequestBody HorarioTarifaCreateDTO dto) {
        log.info("POST /api/horarios-tarifa");
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioTarifasService.create(dto));
    }

    @Operation(summary = "Eliminar horario", description = "Elimina un horario de tarifa por su ID")
    @ApiResponse(responseCode = "204", description = "Horario eliminado")
    @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/horarios-tarifa/{}", id);
        horarioTarifasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
