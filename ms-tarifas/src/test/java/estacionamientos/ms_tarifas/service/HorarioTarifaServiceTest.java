package estacionamientos.ms_tarifas.service;

import estacionamientos.ms_tarifas.dto.HorarioTarifaResponseDTO;
import estacionamientos.ms_tarifas.dto.TarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.TarifaResponseDTO;
import estacionamientos.ms_tarifas.exception.ResourceNotFoundException;
import estacionamientos.ms_tarifas.model.HorarioTarifas;
import estacionamientos.ms_tarifas.model.Tarifas;
import estacionamientos.ms_tarifas.repository.HorarioTarifasRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HorarioTarifasServiceTest {

    @Mock private HorarioTarifasRepository horarioTarifasRepository;
    @Mock private TarifasRepository tarifasRepository;

    @InjectMocks
    private TarifasService tarifasService;
    @InjectMocks
    private HorarioTarifasService horarioTarifasService;

    private Tarifas tarifaActiva;
    private HorarioTarifas horarioTarifaVigente;

    @BeforeEach
    void setUp() {
        LocalDateTime t1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0, 0));  // 09:00
        LocalDateTime t2 = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0, 0));  // 18:00
        tarifaActiva = new Tarifas(1L, "Tarifa Normal", "Tarifa estándar", new BigDecimal("1500.00"), true);
        horarioTarifaVigente = new HorarioTarifas(Long.valueOf(1), tarifaActiva,  "fds", t1, t2, BigDecimal.valueOf(0.5));
    }

    @Test
    @DisplayName("Retorna todos los horarios de tarifa registrados")
    void findAll_debeRetornarListaDeDTOs() {
        // Arrange
        when(horarioTarifasRepository.findAll()).thenReturn(List.of(horarioTarifaVigente));

        // Act
        List<HorarioTarifaResponseDTO> resultado = horarioTarifasService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTarifa().getNombre()).isEqualTo("Tarifa Normal");
    }


//      // Retorna el horario cuya ventana de tiempo contiene el instante actual
//     public HorarioTarifaResponseDTO findVigente() {
//         LocalDateTime ahora = LocalDateTime.now();
//         return horarioTarifasRepository.findAll().stream()
//                 .filter(h -> !ahora.isBefore(h.getHoraInicio()) && !ahora.isAfter(h.getHoraFin()))
//                 .findFirst()
//                 .map(this::toDTO)
//                 .orElseThrow(() -> new ResourceNotFoundException("No hay horario de tarifa vigente para el momento actual"));
//     }

//     // Retorna todos los horarios de tarifa registrados
//     public List<HorarioTarifaResponseDTO> findAll() {
//         log.info("Obteniendo todos los horarios de tarifa");
//         return horarioTarifasRepository.findAll().stream()
//                 .map(this::toDTO)
//                 .toList();
//     }

//     // Busca un horario por id, lanza 404 si no existe
//     public HorarioTarifaResponseDTO findById(Long id) {
//         log.info("Buscando horario de tarifa con id: {}", id);
//         HorarioTarifas horario = horarioTarifasRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Horario de tarifa no encontrado con id: " + id));
//         return toDTO(horario);
//     }

//     // Crea un nuevo horario asociado a una tarifa existente
//     @Transactional
//     public HorarioTarifaResponseDTO create(HorarioTarifaCreateDTO dto) {
//         log.info("Creando horario de tarifa para tarifa id: {}", dto.getIdTarifa());
//         Tarifas tarifa = tarifasService.findEntityById(dto.getIdTarifa());
//         HorarioTarifas horario = new HorarioTarifas();
//         horario.setTarifa(tarifa);
//         horario.setDiaTipo(dto.getDiaTipo());
//         horario.setHoraInicio(dto.getHoraInicio());
//         horario.setHoraFin(dto.getHoraFin());
//         horario.setMultiplicador(BigDecimal.valueOf(dto.getMultiplicador()));
//         HorarioTarifas guardado = horarioTarifasRepository.save(horario);
//         log.info("Horario de tarifa creado con id: {}", guardado.getId());
//         return toDTO(guardado);
//     }

//     // Elimina un horario de tarifa por id
//     @Transactional
//     public void delete(Long id) {
//         log.info("Eliminando horario de tarifa con id: {}", id);
//         if (!horarioTarifasRepository.existsById(id)) {
//             throw new ResourceNotFoundException("Horario de tarifa no encontrado con id: " + id);
//         }
//         horarioTarifasRepository.deleteById(id);
//         log.info("Horario de tarifa eliminado con id: {}", id);
//     }

//     private HorarioTarifaResponseDTO toDTO(HorarioTarifas horario) {
//         return new HorarioTarifaResponseDTO(
//                 horario.getId(),
//                 tarifasService.toDTO(horario.getTarifa()),
//                 horario.getDiaTipo(),
//                 horario.getHoraInicio(),
//                 horario.getHoraFin(),
//                 horario.getMultiplicador().doubleValue());
//     }
// }
}