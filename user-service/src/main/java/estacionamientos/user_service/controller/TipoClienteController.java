package estacionamientos.user_service.controller;

import estacionamientos.user_service.dto.TipoClienteResponseDTO;
import estacionamientos.user_service.service.TipoClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-cliente")
public class TipoClienteController {

    private static final Logger log = LoggerFactory.getLogger(TipoClienteController.class);

    private final TipoClienteService tipoClienteService;

    public TipoClienteController(TipoClienteService tipoClienteService) {
        this.tipoClienteService = tipoClienteService;
    }

    // Retorna todos los tipos de cliente disponibles
    @GetMapping
    public ResponseEntity<List<TipoClienteResponseDTO>> getAll() {
        log.info("GET /api/tipo-cliente");
        return ResponseEntity.ok(tipoClienteService.findAll());
    }

    // Busca un tipo de cliente por id
    @GetMapping("/{id}")
    public ResponseEntity<TipoClienteResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tipo-cliente/{}", id);
        return ResponseEntity.ok(tipoClienteService.findById(id));
    }
}