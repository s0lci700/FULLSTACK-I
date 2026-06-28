package estacionamientos.ms_pagos.service;

import estacionamientos.ms_pagos.dto.BancoDTO;
import estacionamientos.ms_pagos.dto.BancoResponseDTO;
import estacionamientos.ms_pagos.exception.BusinessException;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.Banco;
import estacionamientos.ms_pagos.repository.BancoRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BancoServiceTest {

    @Mock
    private BancoRepository bancoRepository;

    @InjectMocks
    private BancoService bancoService;

    private Banco banco;

    @BeforeEach
    void setUp() {
        banco = new Banco(1L, "Banco Estado", "BCE", new BigDecimal("5.00"), true);
    }

    @Test
    @DisplayName("findAll debe retornar lista de bancos como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        when(bancoRepository.findAll()).thenReturn(List.of(banco));

        List<BancoResponseDTO> resultado = bancoService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Banco Estado");
        assertThat(resultado.get(0).getDescuento()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    @DisplayName("findById debe retornar el banco cuando existe")
    void findById_existente_debeRetornarDTO() {
        when(bancoRepository.findById(1L)).thenReturn(Optional.of(banco));

        BancoResponseDTO resultado = bancoService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Banco Estado");
    }

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        when(bancoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bancoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create debe guardar el banco cuando el nombre no está duplicado")
    void create_nombreNuevo_debeGuardarYRetornarDTO() {
        BancoDTO dto = new BancoDTO("BCI", new BigDecimal("3.00"));
        when(bancoRepository.existsByNombre("BCI")).thenReturn(false);
        when(bancoRepository.save(any(Banco.class))).thenAnswer(inv -> {
            Banco b = inv.getArgument(0);
            b.setId(2L);
            return b;
        });

        BancoResponseDTO resultado = bancoService.create(dto);

        assertThat(resultado.getNombre()).isEqualTo("BCI");
        verify(bancoRepository).save(any(Banco.class));
    }

    @Test
    @DisplayName("create debe lanzar BusinessException cuando el nombre está duplicado")
    void create_nombreDuplicado_debeLanzarBusinessException() {
        BancoDTO dto = new BancoDTO("Banco Estado", new BigDecimal("5.00"));
        when(bancoRepository.existsByNombre("Banco Estado")).thenReturn(true);

        assertThatThrownBy(() -> bancoService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Banco Estado");
    }

    @Test
    @DisplayName("update debe actualizar el banco cuando existe")
    void update_existente_debeActualizarYRetornarDTO() {
        BancoDTO dto = new BancoDTO("Banco Estado Actualizado", new BigDecimal("6.00"));
        when(bancoRepository.findById(1L)).thenReturn(Optional.of(banco));
        when(bancoRepository.save(any(Banco.class))).thenAnswer(inv -> inv.getArgument(0));

        BancoResponseDTO resultado = bancoService.update(1L, dto);

        assertThat(resultado.getNombre()).isEqualTo("Banco Estado Actualizado");
    }

    @Test
    @DisplayName("delete debe eliminar el banco cuando existe")
    void delete_existente_debeEliminar() {
        when(bancoRepository.existsById(1L)).thenReturn(true);

        bancoService.delete(1L);

        verify(bancoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete debe lanzar ResourceNotFoundException cuando no existe")
    void delete_noExiste_debeLanzarNotFound() {
        when(bancoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bancoService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
