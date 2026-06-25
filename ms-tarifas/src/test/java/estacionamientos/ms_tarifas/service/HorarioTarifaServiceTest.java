package estacionamientos.ms_tarifas.service;

import estacionamientos.ms_tarifas.dto.HorarioTarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.HorarioTarifaResponseDTO;
import estacionamientos.ms_tarifas.dto.TarifaResponseDTO;
import estacionamientos.ms_tarifas.exception.ResourceNotFoundException;
import estacionamientos.ms_tarifas.model.HorarioTarifas;
import estacionamientos.ms_tarifas.model.Tarifas;
import estacionamientos.ms_tarifas.repository.HorarioTarifasRepository;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HorarioTarifasServiceTest {

    @Mock private HorarioTarifasRepository horarioTarifasRepository;
    @Mock private TarifasService tarifasService;

    @InjectMocks
    private HorarioTarifasService horarioTarifasService;

    private Tarifas tarifaActiva;
    private HorarioTarifas horarioTarifaVigente;
    private TarifaResponseDTO tarifaDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime t1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime t2 = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0));
        tarifaActiva = new Tarifas(1L, "Tarifa Normal", "Tarifa estándar", new BigDecimal("1500.00"), true);
        horarioTarifaVigente = new HorarioTarifas(1L, tarifaActiva, "fds", t1, t2, BigDecimal.valueOf(0.5));
        tarifaDTO = new TarifaResponseDTO(1L, "Tarifa Normal", "Tarifa estándar", 1500.0, true);
    }

    @Test
    @DisplayName("findAll retorna todos los horarios de tarifa como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        when(horarioTarifasRepository.findAll()).thenReturn(List.of(horarioTarifaVigente));
        when(tarifasService.toDTO(tarifaActiva)).thenReturn(tarifaDTO);

        List<HorarioTarifaResponseDTO> resultado = horarioTarifasService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTarifa().getNombre()).isEqualTo("Tarifa Normal");
    }

    @Test
    @DisplayName("findById retorna el horario cuando existe")
    void findById_existente_debeRetornarDTO() {
        when(horarioTarifasRepository.findById(1L)).thenReturn(Optional.of(horarioTarifaVigente));
        when(tarifasService.toDTO(tarifaActiva)).thenReturn(tarifaDTO);

        HorarioTarifaResponseDTO resultado = horarioTarifasService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById lanza ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        when(horarioTarifasRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> horarioTarifasService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create guarda un nuevo horario y retorna el DTO")
    void create_debeGuardarYRetornarDTO() {
        LocalDateTime inicio = LocalDateTime.now().minusHours(1);
        LocalDateTime fin = LocalDateTime.now().plusHours(1);
        HorarioTarifaCreateDTO dto = new HorarioTarifaCreateDTO(1L, "LABORAL", inicio, fin, 1.0);
        HorarioTarifas guardado = new HorarioTarifas(2L, tarifaActiva, "LABORAL", inicio, fin, BigDecimal.ONE);

        when(tarifasService.findEntityById(1L)).thenReturn(tarifaActiva);
        when(horarioTarifasRepository.save(any())).thenReturn(guardado);
        when(tarifasService.toDTO(any(Tarifas.class))).thenReturn(tarifaDTO);

        HorarioTarifaResponseDTO resultado = horarioTarifasService.create(dto);

        assertThat(resultado.getId()).isEqualTo(2L);
        verify(horarioTarifasRepository).save(any());
    }

    @Test
    @DisplayName("delete elimina el horario cuando existe")
    void delete_existente_debeEliminar() {
        when(horarioTarifasRepository.existsById(1L)).thenReturn(true);

        horarioTarifasService.delete(1L);

        verify(horarioTarifasRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete lanza ResourceNotFoundException cuando no existe")
    void delete_noExiste_debeLanzarNotFound() {
        when(horarioTarifasRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> horarioTarifasService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findVigente retorna el horario cuya ventana contiene el momento actual")
    void findVigente_debeRetornarHorarioVigente() {
        LocalDateTime inicio = LocalDateTime.now().minusHours(1);
        LocalDateTime fin = LocalDateTime.now().plusHours(1);
        HorarioTarifas horarioDia = new HorarioTarifas(3L, tarifaActiva, "LABORAL", inicio, fin, BigDecimal.ONE);

        when(horarioTarifasRepository.findAll()).thenReturn(List.of(horarioDia));
        when(tarifasService.toDTO(tarifaActiva)).thenReturn(tarifaDTO);

        HorarioTarifaResponseDTO resultado = horarioTarifasService.findVigente();

        assertThat(resultado.getId()).isEqualTo(3L);
    }
}
