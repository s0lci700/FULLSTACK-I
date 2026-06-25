package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.SuscripcionResponseDTO;
import estacionamientos.user_service.exception.ResourceNotFoundException;
import estacionamientos.user_service.model.Suscripcion;
import estacionamientos.user_service.repository.SuscripcionRepository;
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
class SuscripcionServiceTest {

    @Mock private SuscripcionRepository suscripcionRepository;

    @InjectMocks
    private SuscripcionService suscripcionService;

    private Suscripcion suscripcion;

    @BeforeEach
    void setUp() {
        suscripcion = new Suscripcion(1L, "Premium", "Plan premium",
                new BigDecimal("9990"), new BigDecimal("10.00"), 30, true);
    }

    @Test
    @DisplayName("findAll retorna todas las suscripciones como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        when(suscripcionRepository.findAll()).thenReturn(List.of(suscripcion));

        List<SuscripcionResponseDTO> resultado = suscripcionService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Premium");
    }

    @Test
    @DisplayName("findById retorna la suscripción cuando existe")
    void findById_existente_debeRetornarDTO() {
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcion));

        SuscripcionResponseDTO resultado = suscripcionService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Premium");
    }

    @Test
    @DisplayName("findById lanza ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        when(suscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> suscripcionService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findEntityById retorna la entidad cuando existe")
    void findEntityById_existente_debeRetornarEntidad() {
        when(suscripcionRepository.findById(1L)).thenReturn(Optional.of(suscripcion));

        Suscripcion resultado = suscripcionService.findEntityById(1L);

        assertThat(resultado.getNombre()).isEqualTo("Premium");
    }

    @Test
    @DisplayName("findEntityById lanza ResourceNotFoundException cuando no existe")
    void findEntityById_noExiste_debeLanzarNotFound() {
        when(suscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> suscripcionService.findEntityById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
