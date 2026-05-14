package estacionamientos.ms_espacios.service;

import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.exception.ResourceNotFoundException;
import estacionamientos.ms_espacios.model.TipoEspacio;
import estacionamientos.ms_espacios.repository.TipoEspacioRepository;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
}