package estacionamientos.ms_tarifas.service;

import estacionamientos.ms_tarifas.dto.HorarioTarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.HorarioTarifaResponseDTO;
import estacionamientos.ms_tarifas.exception.ResourceNotFoundException;
import estacionamientos.ms_tarifas.model.HorarioTarifas;
import estacionamientos.ms_tarifas.model.Tarifas;
import estacionamientos.ms_tarifas.repository.HorarioTarifasRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class HorarioTarifasService {



    private final HorarioTarifasRepository horarioTarifasRepository;
    private final TarifasService tarifasService;

    public HorarioTarifasService(HorarioTarifasRepository horarioTarifasRepository,
            TarifasService tarifasService) {
        this.horarioTarifasRepository = horarioTarifasRepository;
        this.tarifasService = tarifasService;
    }

    // Retorna el horario cuya ventana de tiempo contiene el instante actual
    public HorarioTarifaResponseDTO findVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return horarioTarifasRepository.findAll().stream()
                .filter(h -> !ahora.isBefore(h.getHoraInicio()) && !ahora.isAfter(h.getHoraFin()))
                .findFirst()
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No hay horario de tarifa vigente para el momento actual"));
    }

    // Retorna todos los horarios de tarifa registrados
    public List<HorarioTarifaResponseDTO> findAll() {
        log.info("Obteniendo todos los horarios de tarifa");
        return horarioTarifasRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // Busca un horario por id, lanza 404 si no existe
    public HorarioTarifaResponseDTO findById(Long id) {
        log.info("Buscando horario de tarifa con id: {}", id);
        HorarioTarifas horario = horarioTarifasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de tarifa no encontrado con id: " + id));
        return toDTO(horario);
    }

    // Crea un nuevo horario asociado a una tarifa existente
    @Transactional
    public HorarioTarifaResponseDTO create(HorarioTarifaCreateDTO dto) {
        log.info("Creando horario de tarifa para tarifa id: {}", dto.getIdTarifa());
        Tarifas tarifa = tarifasService.findEntityById(dto.getIdTarifa());
        HorarioTarifas horario = new HorarioTarifas();
        horario.setTarifa(tarifa);
        horario.setDiaTipo(dto.getDiaTipo());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        horario.setMultiplicador(BigDecimal.valueOf(dto.getMultiplicador()));
        HorarioTarifas guardado = horarioTarifasRepository.save(horario);
        log.info("Horario de tarifa creado con id: {}", guardado.getId());
        return toDTO(guardado);
    }

    // Elimina un horario de tarifa por id
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando horario de tarifa con id: {}", id);
        if (!horarioTarifasRepository.existsById(id)) {
            throw new ResourceNotFoundException("Horario de tarifa no encontrado con id: " + id);
        }
        horarioTarifasRepository.deleteById(id);
        log.info("Horario de tarifa eliminado con id: {}", id);
    }

    private HorarioTarifaResponseDTO toDTO(HorarioTarifas horario) {
        return new HorarioTarifaResponseDTO(
                horario.getId(),
                tarifasService.toDTO(horario.getTarifa()),
                horario.getDiaTipo(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getMultiplicador().doubleValue());
    }
}