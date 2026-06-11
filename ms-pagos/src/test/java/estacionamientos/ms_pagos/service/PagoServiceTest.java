package estacionamientos.ms_pagos.service;

import estacionamientos.ms_pagos.dto.TarifaResponseDTO;
import estacionamientos.ms_pagos.exception.BusinessException;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.Cobro;
import estacionamientos.ms_pagos.repository.CobroRepository;
import estacionamientos.ms_pagos.repository.MetodoPagoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private CobroRepository cobroRepository;

    @Mock
    private MetodoPagoRepository metodoPagoRepository;

    @InjectMocks
    private PagoService pagoService;

    // ── Fórmula: monto base ──────────────────────────────────────────────────

    @Test
    @DisplayName("calcularMontoBase: precio×multiplicador×factores×(minutos/60) con HALF_UP")
    void calcularMontoBase_aplicaFormulaCompleta() {
        // Arrange: $1000/hora · horario x2.0 · vehículo x1.5 · espacio x1.2 · 90 min
        TarifaResponseDTO tarifa = new TarifaResponseDTO();
        tarifa.setPrecioBaseHora(1000f);

        // Act (método privado de la fórmula, invocado vía ReflectionTestUtils)
        BigDecimal base = ReflectionTestUtils.invokeMethod(pagoService, "calcularMontoBase",
                tarifa, new BigDecimal("1.5"), new BigDecimal("1.2"), new BigDecimal("2.0"), 90);

        // Assert: 1000 × 2.0 × 1.5 × 1.2 × 1.5h = 5400.00
        assertThat(base).isEqualByComparingTo("5400.00");
    }

    @Test
    @DisplayName("calcularMontoBase: con 0 minutos el monto base es 0.00")
    void calcularMontoBase_ceroMinutos_daCero() {
        TarifaResponseDTO tarifa = new TarifaResponseDTO();
        tarifa.setPrecioBaseHora(1000f);

        BigDecimal base = ReflectionTestUtils.invokeMethod(pagoService, "calcularMontoBase",
                tarifa, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 0);

        assertThat(base).isEqualByComparingTo("0.00");
    }

    // ── Fórmula: monto final con descuentos en cascada ───────────────────────

    @Test
    @DisplayName("calcularMontoFinal: descuentos cliente/suscripción/banco se aplican en cascada")
    void calcularMontoFinal_aplicaDescuentosEnCascada() {
        // Arrange: base $10.000 · cliente 10% · suscripción 20% · banco 5%
        // Act
        BigDecimal montoFinal = ReflectionTestUtils.invokeMethod(pagoService, "calcularMontoFinal",
                new BigDecimal("10000"), new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("5"));

        // Assert: 10000 × 0.90 × 0.80 × 0.95 = 6840.00
        assertThat(montoFinal).isEqualByComparingTo("6840.00");
    }

    @Test
    @DisplayName("calcularMontoFinal: sin descuentos el monto final es igual al base")
    void calcularMontoFinal_sinDescuentos_igualAlBase() {
        BigDecimal montoFinal = ReflectionTestUtils.invokeMethod(pagoService, "calcularMontoFinal",
                new BigDecimal("3500.50"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(montoFinal).isEqualByComparingTo("3500.50");
    }

    // ── Reglas de negocio ────────────────────────────────────────────────────

    @Test
    @DisplayName("crear: lanza BusinessException si el acceso ya tiene cobro (UNIQUE 1:1)")
    void crear_accesoConCobroExistente_lanzaBusinessException() {
        // Arrange
        org.mockito.Mockito.when(cobroRepository.findByIdAcceso(7L)).thenReturn(Optional.of(new Cobro()));
        estacionamientos.ms_pagos.dto.CobroCreateDTO dto = new estacionamientos.ms_pagos.dto.CobroCreateDTO();
        dto.setIdAcceso(7L);

        // Act + Assert
        assertThatThrownBy(() -> pagoService.crear(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un cobro");
    }

    @Test
    @DisplayName("findById: lanza ResourceNotFoundException cuando el cobro no existe")
    void findById_inexistente_lanzaNotFound() {
        org.mockito.Mockito.when(cobroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
