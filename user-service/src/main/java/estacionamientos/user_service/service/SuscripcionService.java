package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.SuscripcionCreateDTO;
import estacionamientos.user_service.dto.SuscripcionResponseDTO;
import estacionamientos.user_service.dto.SuscripcionUpdateDTO;
import estacionamientos.user_service.exception.ConflictException;
import estacionamientos.user_service.exception.ResourceNotFoundException;
import estacionamientos.user_service.model.Suscripcion;
import estacionamientos.user_service.repository.SuscripcionRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SuscripcionService {

    @Autowired
    SuscripcionRepository suscripcionRepository;

    public SuscripcionService(SuscripcionRepository suscripcionRepository) {
        this.suscripcionRepository = suscripcionRepository;
    }

    // Retorna todas las suscripciones disponibles
    public List<SuscripcionResponseDTO> findAll() {
        log.info("Obteniendo todas las suscripciones");
        return suscripcionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // Busca suscripcion por id, lanza 404 si no existe
    public SuscripcionResponseDTO findById(Long id) {
        log.info("Buscando suscripcion con id: {}", id);
        Suscripcion suscripcion = suscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripcion no encontrada con id: " + id));
        return toDTO(suscripcion);
    }

    public SuscripcionResponseDTO toDTO(Suscripcion suscripcion) {
        return new SuscripcionResponseDTO(
                suscripcion.getId(),
                suscripcion.getNombre(),
                suscripcion.getDescripcion(),
                suscripcion.getDescuentoPct(),
                suscripcion.getActivo()
        );
    }

    // Retorna entidad directamente — usado por ClienteSuscripcionService
    public Suscripcion findEntityById(Long id) {
        return suscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripcion no encontrada con id: " + id));
    }

    @Transactional
    public SuscripcionResponseDTO create(SuscripcionCreateDTO dto) {
        log.info("Creando suscripcion nombre: {}", dto.getNombre());
        if (suscripcionRepository.existsByNombre(dto.getNombre())) {
            throw new ConflictException("Ya existe una suscripcion con el nombre: " + dto.getNombre());
        }
        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setNombre(dto.getNombre());
        suscripcion.setDescripcion(dto.getDescripcion());
        suscripcion.setPrecio(dto.getPrecio());
        suscripcion.setDescuentoPct(dto.getDescuentoPct());
        suscripcion.setDuracionDias(dto.getDuracionDias());
        suscripcion.setActivo(true);
        Suscripcion guardado = suscripcionRepository.save(suscripcion);
        log.info("Suscripcion creada con id: {}", guardado.getId());
        return toDTO(guardado);
    }

    @Transactional
    public SuscripcionResponseDTO update(Long id, SuscripcionUpdateDTO dto) {
        log.info("Actualizando suscripcion con id: {}", id);
        Suscripcion suscripcion = suscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripcion no encontrada con id: " + id));
        if (!suscripcion.getNombre().equals(dto.getNombre()) && suscripcionRepository.existsByNombre(dto.getNombre())) {
            throw new ConflictException("Ya existe una suscripcion con el nombre: " + dto.getNombre());
        }
        suscripcion.setNombre(dto.getNombre());
        suscripcion.setDescripcion(dto.getDescripcion());
        suscripcion.setPrecio(dto.getPrecio());
        suscripcion.setDescuentoPct(dto.getDescuentoPct());
        suscripcion.setDuracionDias(dto.getDuracionDias());
        suscripcion.setActivo(dto.getActivo());
        Suscripcion actualizado = suscripcionRepository.save(suscripcion);
        log.info("Suscripcion actualizada con id: {}", actualizado.getId());
        return toDTO(actualizado);
    }

    // Eliminacion logica — marca activo=false (esta entidad tiene flag activo, igual que Cliente)
    @Transactional
    public void delete(Long id) {
        log.info("Desactivando suscripcion con id: {}", id);
        Suscripcion suscripcion = suscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripcion no encontrada con id: " + id));
        suscripcion.setActivo(false);
        suscripcionRepository.save(suscripcion);
        log.info("Suscripcion desactivada con id: {}", id);
    }
}