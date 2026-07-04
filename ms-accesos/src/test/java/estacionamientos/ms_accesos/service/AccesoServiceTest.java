package estacionamientos.ms_accesos.service;

import estacionamientos.ms_accesos.client.EspacioClient;
import estacionamientos.ms_accesos.client.ReservaClient;
import estacionamientos.ms_accesos.dto.AccesoCreateDTO;
import estacionamientos.ms_accesos.dto.AccesoResponseDTO;
import estacionamientos.ms_accesos.dto.ReservaResponseDTO;
import estacionamientos.ms_accesos.exception.ConflictException;
import estacionamientos.ms_accesos.exception.NotFoundException;
import estacionamientos.ms_accesos.model.Acceso;
import estacionamientos.ms_accesos.model.EstadoEnum;
import estacionamientos.ms_accesos.repository.AccesoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccesoServiceTest {

    @Mock private AccesoRepository accesoRepository;
    @Mock private EspacioClient espacioClient;
    @Mock private ReservaClient reservaClient;

    @InjectMocks
    private AccesoService accesoService;

    private AccesoCreateDTO createDTO;
    private Acceso accesoActivo;
    private ReservaResponseDTO reservaConfirmada;

    @BeforeEach
    void setUp() {
        createDTO = new AccesoCreateDTO(1L, 1L, "AB1234");
        reservaConfirmada = new ReservaResponseDTO(1L, 1L, 1L, "CONFIRMADA");

        accesoActivo = new Acceso();
        accesoActivo.setId(1L);
        accesoActivo.setIdReserva(1L);
        accesoActivo.setIdEspacio(1L);
        accesoActivo.setIdVehiculo(1L);
        accesoActivo.setPatenteEscaneada("AB1234");
        accesoActivo.setFechaHoraEntrada(LocalDateTime.now().minusHours(1));
        accesoActivo.setEstado(EstadoEnum.ACTIVO);
    }

    // ── registrarEntrada ──────────────────────────────────────────────────────

    @Test
    @DisplayName("registrarEntrada debe lanzar ConflictException cuando la reserva no está CONFIRMADA")
    void registrarEntrada_reservaNoConfirmada_debeLanzarConflict() {
        // Arrange
        ReservaResponseDTO reservaPendiente = new ReservaResponseDTO(1L, 1L, 1L, "PENDIENTE");
        when(reservaClient.findById(1L)).thenReturn(reservaPendiente);

        // Act + Assert
        assertThatThrownBy(() -> accesoService.registrarEntrada(createDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("no válida");
        verify(accesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarEntrada debe lanzar ConflictException cuando ya existe un acceso para la reserva")
    void registrarEntrada_accesoYaExiste_debeLanzarConflict() {
        // Arrange
        when(reservaClient.findById(1L)).thenReturn(reservaConfirmada);
        when(accesoRepository.findByIdReserva(1L)).thenReturn(Optional.of(accesoActivo));

        // Act + Assert
        assertThatThrownBy(() -> accesoService.registrarEntrada(createDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ya existe");
        verify(accesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarEntrada debe crear el acceso y marcar el espacio no disponible")
    void registrarEntrada_valido_debeCrearAccesoYBloquearEspacio() {
        // Arrange
        when(reservaClient.findById(1L)).thenReturn(reservaConfirmada);
        when(accesoRepository.findByIdReserva(1L)).thenReturn(Optional.empty());
        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> {
            Acceso a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });

        // Act
        AccesoResponseDTO resultado = accesoService.registrarEntrada(createDTO);

        // Assert
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getEstado()).isEqualTo("ACTIVO");
        verify(espacioClient).updateDisponibilidad(1L, false);
    }

    // ── registrarSalida ───────────────────────────────────────────────────────

    @Test
    @DisplayName("registrarSalida debe lanzar NotFoundException cuando el acceso no existe")
    void registrarSalida_noExiste_debeLanzarNotFound() {
        // Arrange
        when(accesoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> accesoService.registrarSalida(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("registrarSalida debe lanzar ConflictException cuando la salida ya fue registrada")
    void registrarSalida_salidaYaRegistrada_debeLanzarConflict() {
        // Arrange
        accesoActivo.setFechaHoraSalida(LocalDateTime.now());
        when(accesoRepository.findById(1L)).thenReturn(Optional.of(accesoActivo));

        // Act + Assert
        assertThatThrownBy(() -> accesoService.registrarSalida(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Salida ya registrada");
    }

    @Test
    @DisplayName("registrarSalida debe calcular minutos, cambiar estado a COMPLETADO y liberar espacio")
    void registrarSalida_valido_debeCalcularMinutosYLiberarEspacio() {
        // Arrange
        when(accesoRepository.findById(1L)).thenReturn(Optional.of(accesoActivo));
        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        AccesoResponseDTO resultado = accesoService.registrarSalida(1L);

        // Assert
        assertThat(resultado.getEstado()).isEqualTo("COMPLETADO");
        assertThat(resultado.getMinutos()).isGreaterThan(0L);
        verify(espacioClient).updateDisponibilidad(1L, true);
        verify(reservaClient).finalizarReserva(1L);
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById debe lanzar NotFoundException cuando el acceso no existe")
    void findById_noExiste_debeLanzarNotFound() {
        // Arrange
        when(accesoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> accesoService.findById(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
