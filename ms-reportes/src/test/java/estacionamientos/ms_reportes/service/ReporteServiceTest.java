package estacionamientos.ms_reportes.service;

import estacionamientos.ms_reportes.client.AccesoClient;
import estacionamientos.ms_reportes.client.CobroClient;
import estacionamientos.ms_reportes.client.EspacioClient;
import estacionamientos.ms_reportes.dto.AccesoResponseDTO;
import estacionamientos.ms_reportes.dto.CobroResponseDTO;
import estacionamientos.ms_reportes.dto.EspacioResponseDTO;
import estacionamientos.ms_reportes.dto.OcupacionReporteDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock private AccesoClient accesoClient;
    @Mock private EspacioClient espacioClient;
    @Mock private CobroClient cobroClient;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    @DisplayName("getOcupacion debe calcular correctamente total, disponibles y ocupados")
    void getOcupacion_debeCalcularEspaciosCorrectamente() {
        // Arrange: 3 espacios — 2 disponibles+activos, 1 ocupado+activo, 0 inactivos
        List<EspacioResponseDTO> espacios = List.of(
                new EspacioResponseDTO(1L, "A-01", "Norte", 1, true,  true),
                new EspacioResponseDTO(2L, "A-02", "Norte", 1, true,  true),
                new EspacioResponseDTO(3L, "B-01", "Sur",   1, false, true)
        );
        when(espacioClient.findAll()).thenReturn(espacios);

        // Act
        OcupacionReporteDTO resultado = reporteService.getOcupacion();

        // Assert
        assertThat(resultado.getTotalEspacios()).isEqualTo(3);
        assertThat(resultado.getDisponibles()).isEqualTo(2);
        assertThat(resultado.getOcupados()).isEqualTo(1);
    }

    @Test
    @DisplayName("getOcupacion debe excluir espacios inactivos del conteo de disponibles")
    void getOcupacion_espacioInactivo_noContaComoDisponible() {
        // Arrange: 2 espacios disponibles pero uno está inactivo
        List<EspacioResponseDTO> espacios = List.of(
                new EspacioResponseDTO(1L, "A-01", "Norte", 1, true, true),
                new EspacioResponseDTO(2L, "A-02", "Norte", 1, true, false) // inactivo
        );
        when(espacioClient.findAll()).thenReturn(espacios);

        // Act
        OcupacionReporteDTO resultado = reporteService.getOcupacion();

        // Assert
        assertThat(resultado.getTotalEspacios()).isEqualTo(2);
        assertThat(resultado.getDisponibles()).isEqualTo(1);
    }

    @Test
    @DisplayName("getAccesoByReserva debe delegar al AccesoClient")
    void getAccesoByReserva_debeDelegarACliente() {
        // Arrange
        AccesoResponseDTO accesoDTO = new AccesoResponseDTO();
        when(accesoClient.findByReserva(5L)).thenReturn(accesoDTO);

        // Act
        AccesoResponseDTO resultado = reporteService.getAccesoByReserva(5L);

        // Assert
        assertThat(resultado).isSameAs(accesoDTO);
        verify(accesoClient).findByReserva(5L);
    }

    @Test
    @DisplayName("getCobrosByCliente debe delegar al CobroClient")
    void getCobrosByCliente_debeDelegarACliente() {
        // Arrange
        List<CobroResponseDTO> cobros = List.of(new CobroResponseDTO());
        when(cobroClient.findByCliente(3L)).thenReturn(cobros);

        // Act
        List<CobroResponseDTO> resultado = reporteService.getCobrosByCliente(3L);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(cobroClient).findByCliente(3L);
    }
}
