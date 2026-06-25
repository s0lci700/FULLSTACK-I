package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.TipoClienteResponseDTO;
import estacionamientos.user_service.exception.ResourceNotFoundException;
import estacionamientos.user_service.model.TipoCliente;
import estacionamientos.user_service.repository.TipoClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoClienteServiceTest {

    @Mock private TipoClienteRepository tipoClienteRepository;

    @InjectMocks
    private TipoClienteService tipoClienteService;

    private TipoCliente tipoCliente;

    @BeforeEach
    void setUp() {
        tipoCliente = new TipoCliente(1L, "Regular", new BigDecimal("5.00"));
    }

    @Test
    @DisplayName("findAll retorna todos los tipos de cliente como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        when(tipoClienteRepository.findAll()).thenReturn(List.of(tipoCliente));

        List<TipoClienteResponseDTO> resultado = tipoClienteService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Regular");
    }

    @Test
    @DisplayName("findById retorna el tipo de cliente cuando existe")
    void findById_existente_debeRetornarDTO() {
        when(tipoClienteRepository.findById(1L)).thenReturn(Optional.of(tipoCliente));

        TipoClienteResponseDTO resultado = tipoClienteService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Regular");
    }

    @Test
    @DisplayName("findById lanza ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        when(tipoClienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoClienteService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findEntityById retorna la entidad cuando existe")
    void findEntityById_existente_debeRetornarEntidad() {
        when(tipoClienteRepository.findById(1L)).thenReturn(Optional.of(tipoCliente));

        TipoCliente resultado = tipoClienteService.findEntityById(1L);

        assertThat(resultado.getNombre()).isEqualTo("Regular");
    }

    @Test
    @DisplayName("findEntityById lanza ResourceNotFoundException cuando no existe")
    void findEntityById_noExiste_debeLanzarNotFound() {
        when(tipoClienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoClienteService.findEntityById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
