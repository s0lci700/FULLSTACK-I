package estacionamientos.ms_tarifas.controller;

import estacionamientos.ms_tarifas.dto.TarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.TarifaResponseDTO;
import estacionamientos.ms_tarifas.dto.TarifaUpdateDTO;
import estacionamientos.ms_tarifas.service.TarifasService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tarifas")
public class TarifasController {


    private final TarifasService tarifasService;

    public TarifasController(TarifasService tarifasService) {
        this.tarifasService = tarifasService;
    }

    // Retorna todas las tarifas registradas
    @GetMapping
    public ResponseEntity<List<TarifaResponseDTO>> getAll() {
        log.info("GET /api/tarifas");
        return ResponseEntity.ok(tarifasService.findAll());
    }

    // Busca una tarifa por id, retorna 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<TarifaResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tarifas/{}", id);
        return ResponseEntity.ok(tarifasService.findById(id));
    }

    // Retorna la tarifa activa actualmente — la consume ms-pagos via Feign
    @GetMapping("/vigente")
    public ResponseEntity<TarifaResponseDTO> getVigente() {
        log.info("GET /api/tarifas/vigente");
        return ResponseEntity.ok(tarifasService.findVigente());
    }

    // Crea una nueva tarifa, valida nombre unico
    // Retorna 201 CREATED con la tarifa creada
    @PostMapping
    public ResponseEntity<TarifaResponseDTO> create(@Valid @RequestBody TarifaCreateDTO dto) {
        log.info("POST /api/tarifas");
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifasService.create(dto));
    }

    // Actualiza los datos de una tarifa existente
    @PutMapping("/{id}")
    public ResponseEntity<TarifaResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody TarifaUpdateDTO dto) {
        log.info("PUT /api/tarifas/{}", id);
        return ResponseEntity.ok(tarifasService.update(id, dto));
    }

    // Eliminacion logica — marca activo=false, no borra de la BD
    // Retorna 204 sin contenido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tarifas/{}", id);
        tarifasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}