package estacionamientos.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import estacionamientos.auth_service.service.AuthService;
import estacionamientos.auth_service.dto.LoginRequestDTO;
import estacionamientos.auth_service.dto.LoginResponseDTO;
import estacionamientos.auth_service.dto.RegisterRequestDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        log.info("POST /api/auth/login — email: {}", dto.getEmail());
        String token = authService.login(dto);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequestDTO dto) {
        log.info("POST /api/auth/register — email: {}", dto.getEmail());
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
        // Lógica para manejar el registro

    }
}