package estacionamientos.ms_reservas.service;

import estacionamientos.ms_reservas.client.ClienteClient;
import estacionamientos.ms_reservas.client.EspacioClient;
import estacionamientos.ms_reservas.client.VehiculoClient;
import estacionamientos.ms_reservas.dto.ClienteResponseDTO;
import estacionamientos.ms_reservas.dto.EspacioResponseDTO;
import estacionamientos.ms_reservas.dto.ReservaCreateDTO;
import estacionamientos.ms_reservas.dto.ReservaResponseDTO;
import estacionamientos.ms_reservas.dto.VehiculoResponseDTO;
import estacionamientos.ms_reservas.exception.ConflictException;
import estacionamientos.ms_reservas.exception.NotFoundException;
import estacionamientos.ms_reservas.model.EstadoEnums;
import estacionamientos.ms_reservas.model.Reserva;
import estacionamientos.ms_reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservasRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private VehiculoClient vehiculoClient;

    @Mock
    private EspacioClient espacioClient;

    @InjectMocks
    private ReservaService reservaService;

    private ReservaCreateDTO createDTO;
    private Reserva reservaPendiente;

    @BeforeEach
    void setUp() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(1);
        LocalDateTime fin = inicio.plusHours(2);
        createDTO = new ReservaCreateDTO(1L, 1L, 1L, inicio, fin);
        reservaPendiente = new Reserva(1L, 1L, 1L, 1L, inicio, fin, EstadoEnums.PENDIENTE, LocalDateTime.now());
    }

    @Test
    @DisplayName("create debe guardar la reserva en estado PENDIENTE cuando cliente, vehículo y espacio son válidos")
    void create_todoValido_debeGuardarPendiente() {
        // Arrange: cliente activo, vehículo activo, espacio activo y disponible
        when(clienteClient.findById(1L)).thenReturn(new ClienteResponseDTO(1L, true));
        when(vehiculoClient.findById(1L)).thenReturn(new VehiculoResponseDTO(1L, 1L, true));
        when(espacioClient.findById(1L)).thenReturn(new EspacioResponseDTO(1L, "A-01", "Norte", 1, 1L, true, true));
        when(reservasRepository.save(any(Reserva.class))).thenAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        // Act
        ReservaResponseDTO resultado = reservaService.create(createDTO);

        // Assert
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getEstado()).isEqualTo(EstadoEnums.PENDIENTE);
        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservasRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoEnums.PENDIENTE);
    }

    @Test
    @DisplayName("create debe lanzar NotFoundException cuando el cliente está inactivo")
    void create_clienteInactivo_debeLanzarNotFound() {
        // Arrange
        when(clienteClient.findById(1L)).thenReturn(new ClienteResponseDTO(1L, false));

        // Act + Assert
        assertThatThrownBy(() -> reservaService.create(createDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Cliente");
        verify(reservasRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("create debe lanzar NotFoundException cuando el espacio no está disponible")
    void create_espacioNoDisponible_debeLanzarNotFound() {
        // Arrange
        when(clienteClient.findById(1L)).thenReturn(new ClienteResponseDTO(1L, true));
        when(vehiculoClient.findById(1L)).thenReturn(new VehiculoResponseDTO(1L, 1L, true));
        when(espacioClient.findById(1L)).thenReturn(new EspacioResponseDTO(1L, "A-01", "Norte", 1, 1L, false, true));

        // Act + Assert
        assertThatThrownBy(() -> reservaService.create(createDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Espacio");
        verify(reservasRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("confirmar debe pasar la reserva a CONFIRMADA y bloquear el espacio")
    void confirmar_pendiente_debeConfirmarYBloquearEspacio() {
        // Arrange
        when(reservasRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));
        when(espacioClient.findById(1L)).thenReturn(new EspacioResponseDTO(1L, "A-01", "Norte", 1, 1L, true, true));
        when(reservasRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ReservaResponseDTO resultado = reservaService.confirmar(1L);

        // Assert
        assertThat(resultado.getEstado()).isEqualTo(EstadoEnums.CONFIRMADA);
        verify(espacioClient).updateDisponibilidad(1L, false);
    }

    @Test
    @DisplayName("confirmar debe lanzar ConflictException si la reserva ya fue cancelada")
    void confirmar_cancelada_debeLanzarConflict() {
        // Arrange
        reservaPendiente.setEstado(EstadoEnums.CANCELADA);
        when(reservasRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));

        // Act + Assert
        assertThatThrownBy(() -> reservaService.confirmar(1L))
                .isInstanceOf(ConflictException.class);
        verify(reservasRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("cancelar debe lanzar ConflictException si la reserva ya fue finalizada")
    void cancelar_finalizada_debeLanzarConflict() {
        // Arrange
        reservaPendiente.setEstado(EstadoEnums.FINALIZADA);
        when(reservasRepository.findById(1L)).thenReturn(Optional.of(reservaPendiente));

        // Act + Assert
        assertThatThrownBy(() -> reservaService.cancelar(1L))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("findById debe lanzar NotFoundException cuando la reserva no existe")
    void findById_inexistente_debeLanzarNotFound() {
        // Arrange
        when(reservasRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> reservaService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Reserva no encontrada");
    }
}
