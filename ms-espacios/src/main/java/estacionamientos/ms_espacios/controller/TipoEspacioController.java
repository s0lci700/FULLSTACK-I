package estacionamientos.ms_espacios.controller;

import java.util.List;

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

import estacionamientos.ms_espacios.dto.TipoEspacioCreateDTO;
import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.dto.TipoEspacioUpdateDTO;
import estacionamientos.ms_espacios.service.TipoEspacioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tipo-espacio")
@Slf4j
@Tag(name = "Tipos de Espacio", description = "Catálogo de tipos de espacio (NORMAL, DISCAPACITADO, MOTO, etc.)")
public class TipoEspacioController {

    private final TipoEspacioService tipoEspaciosService;

    public TipoEspacioController(TipoEspacioService tipoEspaciosService) {
        this.tipoEspaciosService = tipoEspaciosService;
    }

    @Operation(summary = "Listar tipos de espacio", description = "Retorna el catálogo completo de tipos de espacio")
    @ApiResponse(responseCode = "200", description = "Listado de tipos de espacio")
    @GetMapping
    public ResponseEntity<List<TipoEspacioResponseDTO>> getAll() {
        log.info("GET /api/tipo-espacio");
        return ResponseEntity.ok(tipoEspaciosService.findAll());
    }

    @Operation(summary = "Obtener tipo de espacio", description = "Busca un tipo de espacio por su ID")
    @ApiResponse(responseCode = "200", description = "Tipo encontrado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<TipoEspacioResponseDTO> getById(@PathVariable Long id) {
        log.info("GET /api/tipo-espacio/{}", id);
        return ResponseEntity.ok(tipoEspaciosService.findById(id));
    }

    @Operation(summary = "Crear tipo de espacio", description = "Registra un nuevo tipo de espacio")
    @ApiResponse(responseCode = "201", description = "Tipo creado correctamente")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PostMapping
    public ResponseEntity<TipoEspacioResponseDTO> create(@Valid @RequestBody TipoEspacioCreateDTO dto) {
        log.info("POST /api/tipo-espacio");
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoEspaciosService.create(dto));
    }

    @Operation(summary = "Actualizar tipo de espacio", description = "Actualiza los datos de un tipo existente")
    @ApiResponse(responseCode = "200", description = "Tipo actualizado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    @PutMapping("/{id}")
    public ResponseEntity<TipoEspacioResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody TipoEspacioUpdateDTO dto) {
        log.info("PUT /api/tipo-espacio/{}", id);
        return ResponseEntity.ok(tipoEspaciosService.update(id, dto));
    }

    @Operation(summary = "Eliminar tipo de espacio", description = "Elimina un tipo de espacio por su ID")
    @ApiResponse(responseCode = "204", description = "Tipo eliminado")
    @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/tipo-espacio/{}", id);
        tipoEspaciosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
