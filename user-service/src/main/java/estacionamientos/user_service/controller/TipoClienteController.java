package estacionamientos.user_service.controller;

import estacionamientos.user_service.dto.TipoClienteResponseDTO;
import estacionamientos.user_service.service.TipoClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tipo-cliente")
@Tag(name = "Tipos de Cliente", description = "Catálogo de tipos de cliente (REGULAR, VIP, etc.)")
public class TipoClienteController {


    private final TipoClienteService tipoClienteService;

    public TipoClienteController(TipoClienteService tipoClienteService) {
        this.tipoClienteService = tipoClienteService;
    }

    @Operation(summary = "Listar tipos de cliente", description = "Retorna todos los tipos de cliente disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de tipos de cliente")
    @GetMapping
    public ResponseEntity<List<TipoClienteResponseDTO>> getAll() {
        log.info("GET /api/tipo-cliente");
        return ResponseEntity.ok(tipoClienteService.findAll());
    }

    @Operation(summary = "Obtener tipo de cliente", description = "Busca un tipo de cliente por su ID")
    @ApiResponse(responseCode = "200", description = "Tipo de cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Tipo de cliente no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<TipoClienteResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tipo-cliente/{}", id);
        return ResponseEntity.ok(tipoClienteService.findById(id));
    }
}