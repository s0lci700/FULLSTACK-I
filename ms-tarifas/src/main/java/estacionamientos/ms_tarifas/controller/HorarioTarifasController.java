package estacionamientos.ms_tarifas.controller;

import estacionamientos.ms_tarifas.dto.HorarioTarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.HorarioTarifaResponseDTO;
import estacionamientos.ms_tarifas.service.HorarioTarifasService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/horarios-tarifa")
public class HorarioTarifasController {



    private final HorarioTarifasService horarioTarifasService;

    public HorarioTarifasController(HorarioTarifasService horarioTarifasService) {
        this.horarioTarifasService = horarioTarifasService;
    }

    // Retorna todos los horarios de tarifa registrados
    @GetMapping
    public ResponseEntity<List<HorarioTarifaResponseDTO>> getAll() {
        log.info("GET /api/horarios-tarifa");
        return ResponseEntity.ok(horarioTarifasService.findAll());
    }

    // Busca un horario por id, retorna 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<HorarioTarifaResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/horarios-tarifa/{}", id);
        return ResponseEntity.ok(horarioTarifasService.findById(id));
    }

    // Crea un nuevo horario asociado a una tarifa existente
    // Retorna 201 CREATED con el horario creado
    @PostMapping
    public ResponseEntity<HorarioTarifaResponseDTO> create(@Valid @RequestBody HorarioTarifaCreateDTO dto) {
        log.info("POST /api/horarios-tarifa");
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioTarifasService.create(dto));
    }

    // Elimina un horario de tarifa por id, retorna 204 sin contenido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/horarios-tarifa/{}", id);
        horarioTarifasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}