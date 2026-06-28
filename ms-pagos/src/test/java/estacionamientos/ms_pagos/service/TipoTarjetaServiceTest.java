package estacionamientos.ms_pagos.service;

import estacionamientos.ms_pagos.dto.TipoTarjetaDTO;
import estacionamientos.ms_pagos.dto.TipoTarjetaResponseDTO;
import estacionamientos.ms_pagos.exception.BusinessException;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.TipoTarjeta;
import estacionamientos.ms_pagos.repository.TipoTarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoTarjetaServiceTest {

    @Mock
    private TipoTarjetaRepository tipoTarjetaRepository;

    @InjectMocks
    private TipoTarjetaService tipoTarjetaService;

    private TipoTarjeta tipoTarjeta;

    @BeforeEach
    void setUp() {
        tipoTarjeta = new TipoTarjeta(1L, "VISA", "VISA");
    }

    @Test
    @DisplayName("findAll debe retornar lista de tipos de tarjeta como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        when(tipoTarjetaRepository.findAll()).thenReturn(List.of(tipoTarjeta));

        List<TipoTarjetaResponseDTO> resultado = tipoTarjetaService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("VISA");
    }

    @Test
    @DisplayName("findById debe retornar el tipo de tarjeta cuando existe")
    void findById_existente_debeRetornarDTO() {
        when(tipoTarjetaRepository.findById(1L)).thenReturn(Optional.of(tipoTarjeta));

        TipoTarjetaResponseDTO resultado = tipoTarjetaService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("VISA");
    }

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        when(tipoTarjetaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoTarjetaService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create debe guardar el tipo de tarjeta cuando el nombre no está duplicado")
    void create_nombreNuevo_debeGuardarYRetornarDTO() {
        TipoTarjetaDTO dto = new TipoTarjetaDTO("MASTERCARD");
        when(tipoTarjetaRepository.existsByNombre("MASTERCARD")).thenReturn(false);
        when(tipoTarjetaRepository.save(any(TipoTarjeta.class))).thenAnswer(inv -> {
            TipoTarjeta t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        TipoTarjetaResponseDTO resultado = tipoTarjetaService.create(dto);

        assertThat(resultado.getNombre()).isEqualTo("MASTERCARD");
        verify(tipoTarjetaRepository).save(any(TipoTarjeta.class));
    }

    @Test
    @DisplayName("create debe lanzar BusinessException cuando el nombre está duplicado")
    void create_nombreDuplicado_debeLanzarBusinessException() {
        TipoTarjetaDTO dto = new TipoTarjetaDTO("VISA");
        when(tipoTarjetaRepository.existsByNombre("VISA")).thenReturn(true);

        assertThatThrownBy(() -> tipoTarjetaService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("VISA");
    }

    @Test
    @DisplayName("delete debe eliminar el tipo de tarjeta cuando existe")
    void delete_existente_debeEliminar() {
        when(tipoTarjetaRepository.existsById(1L)).thenReturn(true);

        tipoTarjetaService.delete(1L);

        verify(tipoTarjetaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete debe lanzar ResourceNotFoundException cuando no existe")
    void delete_noExiste_debeLanzarNotFound() {
        when(tipoTarjetaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> tipoTarjetaService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
