package estacionamientos.ms_espacios.service;

import estacionamientos.ms_espacios.dto.EspacioCreateDTO;
import estacionamientos.ms_espacios.dto.EspacioResponseDTO;
import estacionamientos.ms_espacios.dto.EspacioUpdateDTO;
import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.exception.ResourceNotFoundException;
import estacionamientos.ms_espacios.model.Espacio;
import estacionamientos.ms_espacios.model.TipoEspacio;
import estacionamientos.ms_espacios.repository.EspacioRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EspacioService {

    private final EspacioRepository espaciosRepository;
    private final TipoEspacioService tipoEspaciosService;

    public EspacioService(EspacioRepository espaciosRepository,
            TipoEspacioService tipoEspaciosService) {
        this.espaciosRepository = espaciosRepository;
        this.tipoEspaciosService = tipoEspaciosService;
    }

    public List<EspacioResponseDTO> findAll() {
        log.info("Obteniendo todos los espacios");
        return espaciosRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public EspacioResponseDTO findById(Long id) {
        log.info("Buscando espacio con id: {}", id);
        Espacio espacio = espaciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espacio no encontrado con id: " + id));
        return toDTO(espacio);
    }

    public List<EspacioResponseDTO> findDisponibles() {
        log.info("Obteniendo espacios disponibles");
        return espaciosRepository.findByDisponibleTrue().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public EspacioResponseDTO create(EspacioCreateDTO dto) {
        log.info("Creando espacio con numero: {}", dto.getNumero());
        if (espaciosRepository.existsByNumero(dto.getNumero())) {
            throw new IllegalArgumentException("Ya existe un espacio con el numero: " + dto.getNumero());
        }
        TipoEspacio tipo = tipoEspaciosService.findEntityById(dto.getIdTipoEspacio());
        Espacio espacio = new Espacio();
        espacio.setNumero(dto.getNumero());
        espacio.setZona(dto.getZona());
        espacio.setPiso(dto.getPiso());
        espacio.setTipoEspacio(tipo);
        espacio.setDisponible(dto.getDisponible());
        espacio.setActivo(dto.getActivo());
        Espacio guardado = espaciosRepository.save(espacio);
        log.info("Espacio creado con id: {}", guardado.getId());
        return toDTO(guardado);
    }

    @Transactional
    public EspacioResponseDTO update(Long id, EspacioUpdateDTO dto) {
        log.info("Actualizando espacio con id: {}", id);
        Espacio espacio = espaciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espacio no encontrado con id: " + id));
        // Validar numero unico solo si cambio
        if (!espacio.getNumero().equals(dto.getNumero())
                && espaciosRepository.existsByNumero(dto.getNumero())) {
            throw new IllegalArgumentException("Ya existe un espacio con el numero: " + dto.getNumero());
        }
        TipoEspacio tipo = tipoEspaciosService.findEntityById(dto.getIdTipoEspacio());
        espacio.setNumero(dto.getNumero());
        espacio.setZona(dto.getZona());
        espacio.setPiso(dto.getPiso());
        espacio.setTipoEspacio(tipo);
        espacio.setActivo(dto.getActivo());
        Espacio actualizado = espaciosRepository.save(espacio);
        log.info("Espacio actualizado con id: {}", actualizado.getId());
        return toDTO(actualizado);
    }

    @Transactional
    public void updateDisponibilidad(Long id, Boolean disponible) {
        log.info("Actualizando disponibilidad del espacio id: {} a: {}", id, disponible);
        Espacio espacio = espaciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espacio no encontrado con id: " + id));
        espacio.setDisponible(disponible);
        espaciosRepository.save(espacio);
        log.info("Disponibilidad actualizada correctamente");
    }

    @Transactional
    public void delete(Long id) {
        log.info("Eliminando espacio con id: {}", id);
        if (!espaciosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Espacio no encontrado con id: " + id);
        }
        EspacioUpdateDTO espacio = espaciosRepository.encontrarPorId(id);
        espacio.setActivo(false);
        log.info("Espacio eliminado con id: {}", id);
    }

    private EspacioResponseDTO toDTO(Espacio espacio) {
        TipoEspacioResponseDTO tipoDTO = tipoEspaciosService.toDTO(espacio.getTipoEspacio());
        return new EspacioResponseDTO(
                espacio.getId(),
                espacio.getNumero(),
                espacio.getZona(),
                espacio.getPiso(),
                tipoDTO,
                espacio.getDisponible(),
                espacio.getActivo());
    }
}