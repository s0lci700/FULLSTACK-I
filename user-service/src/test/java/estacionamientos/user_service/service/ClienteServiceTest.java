package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.ClienteCreateDTO;
import estacionamientos.user_service.dto.ClienteResponseDTO;
import estacionamientos.user_service.dto.ClienteUpdateDTO;
import estacionamientos.user_service.dto.TipoClienteResponseDTO;
import estacionamientos.user_service.exception.ConflictException;
import estacionamientos.user_service.exception.ResourceNotFoundException;
import estacionamientos.user_service.model.Cliente;
import estacionamientos.user_service.model.TipoCliente;
import estacionamientos.user_service.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private TipoClienteService tipoClienteService;

    @InjectMocks
    private ClienteService clienteService;

    private TipoCliente tipoCliente;
    private TipoClienteResponseDTO tipoClienteDTO;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        tipoCliente = new TipoCliente(1L, "ESTANDAR", new BigDecimal("0.00"));
        tipoClienteDTO = new TipoClienteResponseDTO(1L, "ESTANDAR", new BigDecimal("0.00"));
        cliente = new Cliente(1L, "12345678-9", "María", "González", "maria@test.cl", "912345678", tipoCliente, null, true);
    }

    @Test
    @DisplayName("findAll debe retornar lista de clientes como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));
        when(tipoClienteService.toDTO(tipoCliente)).thenReturn(tipoClienteDTO);

        // Act
        List<ClienteResponseDTO> resultado = clienteService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("maria@test.cl");
    }

    @Test
    @DisplayName("findById debe retornar el cliente cuando existe")
    void findById_existente_debeRetornarDTO() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(tipoClienteService.toDTO(tipoCliente)).thenReturn(tipoClienteDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.findById(1L);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("María");
    }

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> clienteService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create debe lanzar ConflictException cuando el email ya está registrado")
    void create_emailDuplicado_debeLanzarConflict() {
        // Arrange
        when(clienteRepository.existsByEmail("maria@test.cl")).thenReturn(true);
        ClienteCreateDTO dto = new ClienteCreateDTO("12345678-9", "María", "González", "maria@test.cl", "912345678", 1L);

        // Act + Assert
        assertThatThrownBy(() -> clienteService.create(dto))
                .isInstanceOf(ConflictException.class);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("create debe guardar el cliente cuando el email es nuevo")
    void create_emailNuevo_debeGuardarYRetornarDTO() {
        // Arrange
        ClienteCreateDTO dto = new ClienteCreateDTO("98765432-1", "Carlos", "Pérez", "carlos@test.cl", "987654321", 1L);
        when(clienteRepository.existsByEmail("carlos@test.cl")).thenReturn(false);
        when(tipoClienteService.findEntityById(1L)).thenReturn(tipoCliente);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });
        when(tipoClienteService.toDTO(tipoCliente)).thenReturn(tipoClienteDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.create(dto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getEmail()).isEqualTo("carlos@test.cl");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("delete debe marcar activo=false sin eliminar la fila")
    void delete_debeDesactivarCliente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        clienteService.delete(1L);

        // Assert
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(captor.capture());
        assertThat(captor.getValue().getActivo()).isFalse();
    }

    @Test
    @DisplayName("delete debe lanzar ResourceNotFoundException cuando el cliente no existe")
    void delete_noExiste_debeLanzarNotFound() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> clienteService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
