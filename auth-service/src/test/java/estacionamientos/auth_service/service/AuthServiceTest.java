package estacionamientos.auth_service.service;

import estacionamientos.auth_service.dto.LoginRequestDTO;
import estacionamientos.auth_service.dto.RegisterRequestDTO;
import estacionamientos.auth_service.exception.ConflictException;
import estacionamientos.auth_service.exception.InvalidCredentialsException;
import estacionamientos.auth_service.exception.NotFoundException;
import estacionamientos.auth_service.model.Rol;
import estacionamientos.auth_service.model.UserCredential;
import estacionamientos.auth_service.repository.RolRepository;
import estacionamientos.auth_service.repository.UserCredentialRepository;
import estacionamientos.auth_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserCredentialRepository userCredentialRepository;
    @Mock private RolRepository rolRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Rol rol;
    private UserCredential credencial;

    @BeforeEach
    void setUp() {
        rol = new Rol(1L, "CLIENTE", "Cliente del sistema");
        credencial = new UserCredential(1L, "sol@test.cl", "hash-bcrypt", null, rol, true);
    }

    // ── login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login debe lanzar InvalidCredentialsException cuando el email no existe")
    void login_emailNoExiste_debeLanzarInvalidCredentials() {
        // Arrange
        when(userCredentialRepository.findByEmail("noexiste@test.cl")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> authService.login(new LoginRequestDTO("noexiste@test.cl", "pass")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("login debe lanzar InvalidCredentialsException cuando la contraseña es incorrecta")
    void login_passwordIncorrecta_debeLanzarInvalidCredentials() {
        // Arrange
        when(userCredentialRepository.findByEmail("sol@test.cl")).thenReturn(Optional.of(credencial));
        when(passwordEncoder.matches("password-mala", "hash-bcrypt")).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> authService.login(new LoginRequestDTO("sol@test.cl", "password-mala")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("login debe retornar token JWT cuando las credenciales son válidas")
    void login_credencialesValidas_debeRetornarToken() {
        // Arrange
        when(userCredentialRepository.findByEmail("sol@test.cl")).thenReturn(Optional.of(credencial));
        when(passwordEncoder.matches("password-ok", "hash-bcrypt")).thenReturn(true);
        when(jwtUtil.generateToken("sol@test.cl", "CLIENTE")).thenReturn("jwt-token");

        // Act
        String token = authService.login(new LoginRequestDTO("sol@test.cl", "password-ok"));

        // Assert
        assertThat(token).isEqualTo("jwt-token");
    }

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register debe lanzar ConflictException cuando el email ya está registrado")
    void register_emailDuplicado_debeLanzarConflict() {
        // Arrange
        when(userCredentialRepository.findByEmail("sol@test.cl")).thenReturn(Optional.of(credencial));

        // Act + Assert
        assertThatThrownBy(() -> authService.register(new RegisterRequestDTO("sol@test.cl", "Pass123!", "CLIENTE")))
                .isInstanceOf(ConflictException.class);
        verify(userCredentialRepository, never()).save(any());
    }

    @Test
    @DisplayName("register debe lanzar NotFoundException cuando el rol no existe")
    void register_rolNoExiste_debeLanzarNotFound() {
        // Arrange
        when(userCredentialRepository.findByEmail("nuevo@test.cl")).thenReturn(Optional.empty());
        when(rolRepository.findByNombre("ROL_INEXISTENTE")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> authService.register(new RegisterRequestDTO("nuevo@test.cl", "Pass123!", "ROL_INEXISTENTE")))
                .isInstanceOf(NotFoundException.class);
        verify(userCredentialRepository, never()).save(any());
    }

    @Test
    @DisplayName("register debe guardar el usuario cuando los datos son válidos")
    void register_datosValidos_debeGuardarUsuario() {
        // Arrange
        when(userCredentialRepository.findByEmail("nuevo@test.cl")).thenReturn(Optional.empty());
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(anyString())).thenReturn("hash-nuevo");

        // Act
        authService.register(new RegisterRequestDTO("nuevo@test.cl", "Pass123!", "CLIENTE"));

        // Assert
        verify(userCredentialRepository).save(any(UserCredential.class));
    }
}
