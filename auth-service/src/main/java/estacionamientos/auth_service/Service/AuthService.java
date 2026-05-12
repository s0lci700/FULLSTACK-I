package estacionamientos.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import estacionamientos.auth_service.repository.RolRepository;
import estacionamientos.auth_service.repository.UserCredentialRepository;
import estacionamientos.auth_service.dto.LoginRequestDTO;
import estacionamientos.auth_service.dto.RegisterRequestDTO;
import estacionamientos.auth_service.exception.ConflictException;
import estacionamientos.auth_service.exception.InvalidCredentialsException;
import estacionamientos.auth_service.exception.NotFoundException;
import estacionamientos.auth_service.model.Rol;
import estacionamientos.auth_service.model.UserCredential;
import estacionamientos.auth_service.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AuthService {
    @Autowired
    UserCredentialRepository userCredentialRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public String login(LoginRequestDTO dto) {
        log.info("Intentando iniciar sesión con email: {}", dto.getEmail());
        UserCredential credencial = userCredentialRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                log.error("Login fallido — email no encontrado: {}", dto.getEmail());
                return new InvalidCredentialsException("Credenciales inválidas");
            });
        if (!passwordEncoder.matches(dto.getPassword(), credencial.getPasswordHash())) {
            log.error("Login fallido — contraseña incorrecta para email: {}", dto.getEmail());
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        log.info("Login exitoso para email: {}", dto.getEmail());
        return jwtUtil.generateToken(credencial.getEmail(), credencial.getRol().getNombre());
    }

    public void register(RegisterRequestDTO dto) {
        log.info("Intentando registrar nuevo usuario con email: {}", dto.getEmail());
        if (userCredentialRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.error("Registro fallido — email ya registrado: {}", dto.getEmail());
            throw new ConflictException("El email ya está registrado");
        }
        Rol rol = rolRepository.findByNombre(dto.getNombreRol())
                .orElseThrow(() -> {
                    log.error("Registro fallido — rol no encontrado: {}", dto.getNombreRol());
                    return new NotFoundException("Rol no encontrado");
                });
        UserCredential credencial = new UserCredential();
        credencial.setEmail(dto.getEmail());
        credencial.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        credencial.setRol(rol);
        credencial.setActivo(true);
        userCredentialRepository.save(credencial);
        log.info("Registro exitoso para email: {}", dto.getEmail());
    }

}
