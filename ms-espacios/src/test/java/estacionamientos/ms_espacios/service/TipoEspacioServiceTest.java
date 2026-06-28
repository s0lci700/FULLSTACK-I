package estacionamientos.ms_espacios.service;

import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.exception.ResourceNotFoundException;
import estacionamientos.ms_espacios.model.TipoEspacio;
import estacionamientos.ms_espacios.repository.TipoEspacioRepository;
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
class TipoEspacioServiceTest {

    @Mock
    private TipoEspacioRepository tipoEspaciosRepository;

    @InjectMocks
    private TipoEspacioService tipoEspacioService;

    private TipoEspacio tipoEspacio;

    @BeforeEach
    void setUp() {
        tipoEspacio = new TipoEspacio(1L, "Normal", "Espacio estándar", new BigDecimal("1.00"));
    }

    @Test
    @DisplayName("findAll debe retornar lista de tipos de espacio como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        when(tipoEspaciosRepository.findAll()).thenReturn(List.of(tipoEspacio));

        List<TipoEspacioResponseDTO> resultado = tipoEspacioService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Normal");
        assertThat(resultado.get(0).getFactorPrecio()).isEqualByComparingTo(new BigDecimal("1.00"));
    }

    @Test
    @DisplayName("findById debe retornar el tipo de espacio cuando existe")
    void findById_existente_debeRetornarDTO() {
        when(tipoEspaciosRepository.findById(1L)).thenReturn(Optional.of(tipoEspacio));

        TipoEspacioResponseDTO resultado = tipoEspacioService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Normal");
    }

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        when(tipoEspaciosRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoEspacioService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findEntityById debe retornar la entidad cuando existe")
    void findEntityById_existente_debeRetornarEntidad() {
        when(tipoEspaciosRepository.findById(1L)).thenReturn(Optional.of(tipoEspacio));

        TipoEspacio resultado = tipoEspacioService.findEntityById(1L);

        assertThat(resultado).isEqualTo(tipoEspacio);
    }

    @Test
    @DisplayName("findEntityById debe lanzar ResourceNotFoundException cuando no existe")
    void findEntityById_noExiste_debeLanzarNotFound() {
        when(tipoEspaciosRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoEspacioService.findEntityById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
