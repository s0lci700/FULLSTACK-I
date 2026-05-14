package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.SuscripcionResponseDTO;
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
                suscripcion.getDescuentoPorcentaje(),
                suscripcion.getActivo()
        );
    }

    // Retorna entidad directamente — usado por ClienteSuscripcionService
    public Suscripcion findEntityById(Long id) {
        return suscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscripcion no encontrada con id: " + id));
    }
}