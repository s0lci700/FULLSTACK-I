package estacionamientos.ms_espacios.service;

import estacionamientos.ms_espacios.dto.EspacioCreateDTO;
import estacionamientos.ms_espacios.dto.EspacioResponseDTO;
import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.exception.ConflictException;
import estacionamientos.ms_espacios.exception.ResourceNotFoundException;
import estacionamientos.ms_espacios.model.Espacio;
import estacionamientos.ms_espacios.model.TipoEspacio;
import estacionamientos.ms_espacios.repository.EspacioRepository;
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
class EspacioServiceTest {

    @Mock
    private EspacioRepository espaciosRepository;

    @Mock
    private TipoEspacioService tipoEspaciosService;

    @InjectMocks
    private EspacioService espacioService;

    private TipoEspacio tipoEspacio;
    private TipoEspacioResponseDTO tipoEspacioDTO;
    private Espacio espacio;

    @BeforeEach
    void setUp() {
        tipoEspacio = new TipoEspacio(1L, "Normal", "Espacio estándar", new BigDecimal("1.00"));
        tipoEspacioDTO = new TipoEspacioResponseDTO(1L, "Normal", "Espacio estándar", new BigDecimal("1.00"));
        espacio = new Espacio(1L, "A-01", "Norte", 1, tipoEspacio, true, true);
    }

    @Test
    @DisplayName("findAll debe retornar la lista de espacios como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        // Arrange
        when(espaciosRepository.findAll()).thenReturn(List.of(espacio));
        when(tipoEspaciosService.toDTO(tipoEspacio)).thenReturn(tipoEspacioDTO);

        // Act
        List<EspacioResponseDTO> resultado = espacioService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumero()).isEqualTo("A-01");
        assertThat(resultado.get(0).getTipoEspacio().getNombre()).isEqualTo("Normal");
    }

    @Test
    @DisplayName("findById debe retornar el espacio cuando existe")
    void findById_existente_debeRetornarDTO() {
        // Arrange
        when(espaciosRepository.findById(1L)).thenReturn(Optional.of(espacio));
        when(tipoEspaciosService.toDTO(tipoEspacio)).thenReturn(tipoEspacioDTO);

        // Act
        EspacioResponseDTO resultado = espacioService.findById(1L);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getDisponible()).isTrue();
    }

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException cuando no existe")
    void findById_inexistente_debeLanzarNotFound() {
        // Arrange
        when(espaciosRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> espacioService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create debe guardar el espacio cuando el número no está duplicado")
    void create_numeroNuevo_debeGuardarYRetornarDTO() {
        // Arrange
        EspacioCreateDTO dto = new EspacioCreateDTO("B-02", "Sur", 2, 1L, true, true);
        when(espaciosRepository.existsByNumero("B-02")).thenReturn(false);
        when(tipoEspaciosService.findEntityById(1L)).thenReturn(tipoEspacio);
        when(espaciosRepository.save(any(Espacio.class)))
                .thenAnswer(inv -> {
                    Espacio e = inv.getArgument(0);
                    e.setId(2L);
                    return e;
                });
        when(tipoEspaciosService.toDTO(tipoEspacio)).thenReturn(tipoEspacioDTO);

        // Act
        EspacioResponseDTO resultado = espacioService.create(dto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getNumero()).isEqualTo("B-02");
        verify(espaciosRepository).save(any(Espacio.class));
    }

    @Test
    @DisplayName("create debe lanzar ConflictException cuando el número está duplicado")
    void create_numeroDuplicado_debeLanzarExcepcion() {
        // Arrange
        EspacioCreateDTO dto = new EspacioCreateDTO("A-01", "Norte", 1, 1L, true, true);
        when(espaciosRepository.existsByNumero("A-01")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> espacioService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("A-01");
        verify(espaciosRepository, never()).save(any(Espacio.class));
    }

    @Test
    @DisplayName("updateDisponibilidad debe cambiar el flag disponible del espacio")
    void updateDisponibilidad_debeActualizarFlag() {
        // Arrange
        when(espaciosRepository.findById(1L)).thenReturn(Optional.of(espacio));
        when(espaciosRepository.save(any(Espacio.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        espacioService.updateDisponibilidad(1L, false);

        // Assert
        ArgumentCaptor<Espacio> captor = ArgumentCaptor.forClass(Espacio.class);
        verify(espaciosRepository).save(captor.capture());
        assertThat(captor.getValue().getDisponible()).isFalse();
    }

    @Test
    @DisplayName("delete debe hacer borrado lógico dejando activo=false")
    void delete_debeDesactivarEspacio() {
        // Arrange
        when(espaciosRepository.findById(1L)).thenReturn(Optional.of(espacio));
        when(espaciosRepository.save(any(Espacio.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        espacioService.delete(1L);

        // Assert
        ArgumentCaptor<Espacio> captor = ArgumentCaptor.forClass(Espacio.class);
        verify(espaciosRepository).save(captor.capture());
        assertThat(captor.getValue().getActivo()).isFalse();
    }
}
