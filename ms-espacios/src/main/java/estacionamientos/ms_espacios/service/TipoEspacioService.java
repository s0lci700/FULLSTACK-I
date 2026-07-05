package estacionamientos.ms_espacios.service;

import estacionamientos.ms_espacios.dto.TipoEspacioCreateDTO;
import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.dto.TipoEspacioUpdateDTO;
import estacionamientos.ms_espacios.exception.ConflictException;
import estacionamientos.ms_espacios.exception.ResourceNotFoundException;
import estacionamientos.ms_espacios.model.TipoEspacio;
import estacionamientos.ms_espacios.repository.TipoEspacioRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TipoEspacioService {


    private final TipoEspacioRepository tipoEspaciosRepository;

    public TipoEspacioService(TipoEspacioRepository tipoEspaciosRepository) {
        this.tipoEspaciosRepository = tipoEspaciosRepository;
    }

    public List<TipoEspacioResponseDTO> findAll() {
        log.info("Obteniendo todos los tipos de espacio");
        return tipoEspaciosRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public TipoEspacioResponseDTO findById(Long id) {
        log.info("Buscando tipo de espacio con id: {}", id);
        TipoEspacio tipo = tipoEspaciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de espacio no encontrado con id: " + id));
        return toDTO(tipo);
    }

    public TipoEspacioResponseDTO toDTO(TipoEspacio tipo) {
        return new TipoEspacioResponseDTO(
                tipo.getId(),
                tipo.getNombre(),
                tipo.getDescripcion(),
                tipo.getFactorPrecio()
        );
    }

    public TipoEspacio findEntityById(Long id) {
        return tipoEspaciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de espacio no encontrado con id: " + id));
    }

    @Transactional
    public TipoEspacioResponseDTO create(TipoEspacioCreateDTO dto) {
        log.info("Creando tipo de espacio nombre: {}", dto.getNombre());
        if (tipoEspaciosRepository.existsByNombre(dto.getNombre())) {
            throw new ConflictException("Ya existe un tipo de espacio con el nombre: " + dto.getNombre());
        }
        TipoEspacio tipo = new TipoEspacio();
        tipo.setNombre(dto.getNombre());
        tipo.setDescripcion(dto.getDescripcion());
        tipo.setFactorPrecio(dto.getFactorPrecio());
        TipoEspacio guardado = tipoEspaciosRepository.save(tipo);
        log.info("Tipo de espacio creado con id: {}", guardado.getId());
        return toDTO(guardado);
    }

    @Transactional
    public TipoEspacioResponseDTO update(Long id, TipoEspacioUpdateDTO dto) {
        log.info("Actualizando tipo de espacio con id: {}", id);
        TipoEspacio tipo = tipoEspaciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de espacio no encontrado con id: " + id));
        if (!tipo.getNombre().equals(dto.getNombre()) && tipoEspaciosRepository.existsByNombre(dto.getNombre())) {
            throw new ConflictException("Ya existe un tipo de espacio con el nombre: " + dto.getNombre());
        }
        tipo.setNombre(dto.getNombre());
        tipo.setDescripcion(dto.getDescripcion());
        tipo.setFactorPrecio(dto.getFactorPrecio());
        TipoEspacio actualizado = tipoEspaciosRepository.save(tipo);
        log.info("Tipo de espacio actualizado con id: {}", actualizado.getId());
        return toDTO(actualizado);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Eliminando tipo de espacio con id: {}", id);
        if (!tipoEspaciosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de espacio no encontrado con id: " + id);
        }
        tipoEspaciosRepository.deleteById(id);
        log.info("Tipo de espacio eliminado con id: {}", id);
    }
}