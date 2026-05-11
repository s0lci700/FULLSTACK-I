package estacionamientos.ms_espacios.service;

import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.exception.ResourceNotFoundException;
import estacionamientos.ms_espacios.model.TipoEspacios;
import estacionamientos.ms_espacios.repository.TipoEspaciosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoEspaciosService {

    private static final Logger log = LoggerFactory.getLogger(TipoEspaciosService.class);

    private final TipoEspaciosRepository tipoEspaciosRepository;

    public TipoEspaciosService(TipoEspaciosRepository tipoEspaciosRepository) {
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
        TipoEspacios tipo = tipoEspaciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de espacio no encontrado con id: " + id));
        return toDTO(tipo);
    }

    public TipoEspacioResponseDTO toDTO(TipoEspacios tipo) {
        return new TipoEspacioResponseDTO(
                tipo.getId(),
                tipo.getNombre(),
                tipo.getDescripcion(),
                tipo.getFactorPrecio()
        );
    }

    public TipoEspacios findEntityById(Long id) {
        return tipoEspaciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de espacio no encontrado con id: " + id));
    }
}