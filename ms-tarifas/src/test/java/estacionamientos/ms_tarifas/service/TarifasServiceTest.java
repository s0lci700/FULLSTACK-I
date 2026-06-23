package estacionamientos.ms_tarifas.service;

import estacionamientos.ms_tarifas.dto.TarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.TarifaResponseDTO;
import estacionamientos.ms_tarifas.exception.ResourceNotFoundException;
import estacionamientos.ms_tarifas.model.Tarifas;
import estacionamientos.ms_tarifas.repository.TarifasRepository;
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
class TarifasServiceTest {

    @Mock private TarifasRepository tarifasRepository;

    @InjectMocks
    private TarifasService tarifasService;

    private Tarifas tarifaActiva;

    @BeforeEach
    void setUp() {
        tarifaActiva = new Tarifas(1L, "Tarifa Normal", "Tarifa estándar", new BigDecimal("1500.00"), true);
    }

    @Test
    @DisplayName("findAll debe retornar todas las tarifas como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        // Arrange
        when(tarifasRepository.findAll()).thenReturn(List.of(tarifaActiva));

        // Act
        List<TarifaResponseDTO> resultado = tarifasService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Tarifa Normal");
    }

    @Test
    @DisplayName("findById debe retornar la tarifa cuando existe")
    void findById_existente_debeRetornarDTO() {
        // Arrange
        when(tarifasRepository.findById(1L)).thenReturn(Optional.of(tarifaActiva));

        // Act
        TarifaResponseDTO resultado = tarifasService.findById(1L);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPrecioBaseHora()).isEqualTo(1500.00);
    }

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        // Arrange
        when(tarifasRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> tarifasService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findVigente debe retornar la tarifa con activo=true")
    void findVigente_conTarifaActiva_debeRetornarDTO() {
        // Arrange
        when(tarifasRepository.findFirstByActivoTrue()).thenReturn(Optional.of(tarifaActiva));

        // Act
        TarifaResponseDTO resultado = tarifasService.findVigente();

        // Assert
        assertThat(resultado.getActivo()).isTrue();
        assertThat(resultado.getNombre()).isEqualTo("Tarifa Normal");
    }

    @Test
    @DisplayName("findVigente debe lanzar ResourceNotFoundException cuando no hay tarifa activa")
    void findVigente_sinTarifaActiva_debeLanzarNotFound() {
        // Arrange
        when(tarifasRepository.findFirstByActivoTrue()).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> tarifasService.findVigente())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hay tarifa activa");
    }

    @Test
    @DisplayName("create debe lanzar IllegalArgumentException cuando el nombre ya existe")
    void create_nombreDuplicado_debeLanzarIllegalArgument() {
        // Arrange
        when(tarifasRepository.existsByNombre("Tarifa Normal")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> tarifasService.create(new TarifaCreateDTO("Tarifa Normal", "desc", 1500.0, true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tarifa Normal");
        verify(tarifasRepository, never()).save(any());
    }

    @Test
    @DisplayName("create debe guardar la tarifa cuando el nombre es nuevo")
    void create_nombreNuevo_debeGuardarYRetornarDTO() {
        // Arrange
        TarifaCreateDTO dto = new TarifaCreateDTO("Tarifa Weekend", "Tarifa fin de semana", 2000.0, true);
        when(tarifasRepository.existsByNombre("Tarifa Weekend")).thenReturn(false);
        when(tarifasRepository.save(any(Tarifas.class))).thenAnswer(inv -> {
            Tarifas t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        // Act
        TarifaResponseDTO resultado = tarifasService.create(dto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getNombre()).isEqualTo("Tarifa Weekend");
    }

    @Test
    @DisplayName("delete debe marcar activo=false (eliminación lógica)")
    void delete_existente_debeDesactivarTarifa() {
        // Arrange
        when(tarifasRepository.findById(1L)).thenReturn(Optional.of(tarifaActiva));
        when(tarifasRepository.save(any(Tarifas.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        tarifasService.delete(1L);

        // Assert
        ArgumentCaptor<Tarifas> captor = ArgumentCaptor.forClass(Tarifas.class);
        verify(tarifasRepository).save(captor.capture());
        assertThat(captor.getValue().getActivo()).isFalse();
    }

    @Test
    @DisplayName("delete debe lanzar ResourceNotFoundException cuando la tarifa no existe")
    void delete_noExiste_debeLanzarNotFound() {
        // Arrange
        when(tarifasRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> tarifasService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
