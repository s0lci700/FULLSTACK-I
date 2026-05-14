package estacionamientos.ms_reservas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.ms_reservas.dto.ReservaCreateDTO;
import estacionamientos.ms_reservas.dto.ReservaResponseDTO;
import estacionamientos.ms_reservas.service.ReservaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservasService;
    
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> findAll() {
        return ResponseEntity.ok(reservasService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.findById(id));
    }
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<ReservaResponseDTO>> findByIdCliente(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.findByIdCliente(id));
    }
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> create(
        @Valid @RequestBody ReservaCreateDTO reserva) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservasService.create(reserva));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.cancelar(id));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponseDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.confirmar(id));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ReservaResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(reservasService.finalizar(id));
    }
}
