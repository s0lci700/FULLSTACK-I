package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.ClienteResponseDTO;
import estacionamientos.user_service.dto.ClienteSuscripcionCreateDTO;
import estacionamientos.user_service.dto.ClienteSuscripcionResponseDTO;
import estacionamientos.user_service.dto.SuscripcionResponseDTO;
import estacionamientos.user_service.dto.TipoClienteResponseDTO;
import estacionamientos.user_service.model.Cliente;
import estacionamientos.user_service.model.ClienteSuscripcion;
import estacionamientos.user_service.model.Suscripcion;
import estacionamientos.user_service.model.TipoCliente;
import estacionamientos.user_service.repository.ClienteSuscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteSuscripcionServiceTest {

    @Mock private ClienteSuscripcionRepository clienteSuscripcionRepository;
    @Mock private ClienteService clienteService;
    @Mock private SuscripcionService suscripcionService;

    @InjectMocks
    private ClienteSuscripcionService clienteSuscripcionService;

    private Cliente cliente;
    private Suscripcion suscripcion;
    private ClienteSuscripcion clienteSuscripcion;
    private ClienteResponseDTO clienteDTO;
    private SuscripcionResponseDTO suscripcionDTO;

    @BeforeEach
    void setUp() {
        TipoCliente tipoCliente = new TipoCliente(1L, "Regular", new BigDecimal("5.00"));
        TipoClienteResponseDTO tipoClienteDTO = new TipoClienteResponseDTO(1L, "Regular", new BigDecimal("5.00"));

        cliente = new Cliente(1L, "12345678-9", "María", "González",
                "maria@test.cl", "912345678", tipoCliente, LocalDateTime.now(), true);
        suscripcion = new Suscripcion(1L, "Premium", "Plan premium",
                new BigDecimal("9990"), new BigDecimal("10.00"), 30, true);
        clienteSuscripcion = new ClienteSuscripcion(1L, cliente, suscripcion,
                LocalDate.now(), LocalDate.now().plusDays(30), true);

        clienteDTO = new ClienteResponseDTO(1L, "María", "González",
                "maria@test.cl", "912345678", tipoClienteDTO, true);
        suscripcionDTO = new SuscripcionResponseDTO(1L, "Premium", "Plan premium",
                new BigDecimal("10.00"), true);
    }

    @Test
    @DisplayName("findByClienteId retorna las suscripciones activas del cliente como DTOs")
    void findByClienteId_debeRetornarLista() {
        when(clienteSuscripcionRepository.findByClienteId(1L)).thenReturn(List.of(clienteSuscripcion));
        when(clienteService.toDTO(cliente)).thenReturn(clienteDTO);
        when(suscripcionService.toDTO(suscripcion)).thenReturn(suscripcionDTO);

        List<ClienteSuscripcionResponseDTO> resultado = clienteSuscripcionService.findByClienteId(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCliente().getNombre()).isEqualTo("María");
    }

    @Test
    @DisplayName("create lanza IllegalArgumentException cuando el cliente ya tiene esa suscripción activa")
    void create_suscripcionDuplicada_debeLanzarIllegalArgument() {
        when(clienteSuscripcionRepository
                .existsByClienteIdAndSuscripcionIdAndActivoTrue(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> clienteSuscripcionService.create(1L,
                new ClienteSuscripcionCreateDTO(1L, LocalDate.now(), LocalDate.now().plusDays(30))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("activa");
    }

    @Test
    @DisplayName("create asigna la suscripción al cliente y retorna el DTO")
    void create_nuevo_debeGuardarYRetornarDTO() {
        when(clienteSuscripcionRepository
                .existsByClienteIdAndSuscripcionIdAndActivoTrue(1L, 1L)).thenReturn(false);
        when(clienteService.findEntityById(1L)).thenReturn(cliente);
        when(suscripcionService.findEntityById(1L)).thenReturn(suscripcion);
        when(clienteSuscripcionRepository.save(any())).thenReturn(clienteSuscripcion);
        when(clienteService.toDTO(cliente)).thenReturn(clienteDTO);
        when(suscripcionService.toDTO(suscripcion)).thenReturn(suscripcionDTO);

        ClienteSuscripcionResponseDTO resultado = clienteSuscripcionService.create(1L,
                new ClienteSuscripcionCreateDTO(1L, LocalDate.now(), LocalDate.now().plusDays(30)));

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getActivo()).isTrue();
        verify(clienteSuscripcionRepository).save(any());
    }
}
