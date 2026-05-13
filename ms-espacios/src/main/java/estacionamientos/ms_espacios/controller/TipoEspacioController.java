package estacionamientos.ms_espacios.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.service.TipoEspacioService;

@RestController
@RequestMapping("/api/tipo-espacio")
public class TipoEspacioController {

    private static final Logger log = LoggerFactory.getLogger(TipoEspacioController.class);

    private final TipoEspacioService tipoEspaciosService;

    public TipoEspacioController(TipoEspacioService tipoEspaciosService) {
        this.tipoEspaciosService = tipoEspaciosService;
    }

    @GetMapping
    public ResponseEntity<List<TipoEspacioResponseDTO>> getAll() {
        log.info("GET /api/tipo-espacio");
        return ResponseEntity.ok(tipoEspaciosService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoEspacioResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tipo-espacio/{}", id);
        return ResponseEntity.ok(tipoEspaciosService.findById(id));
    }
}