package estacionamientos.user_service.controller;

import estacionamientos.user_service.dto.SuscripcionResponseDTO;
import estacionamientos.user_service.service.SuscripcionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/suscripciones")
public class SuscripcionController {

    @Autowired
    private SuscripcionService suscripcionService;

    @GetMapping
    public ResponseEntity<List<SuscripcionResponseDTO>> getAll() {
        log.info("GET /api/suscripciones");
        return ResponseEntity.ok(suscripcionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/suscripciones/{}", id);
        return ResponseEntity.ok(suscripcionService.findById(id));
    }
}
