package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.TipoClienteResponseDTO;
import estacionamientos.user_service.exception.ResourceNotFoundException;
import estacionamientos.user_service.model.TipoCliente;
import estacionamientos.user_service.repository.TipoClienteRepository;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TipoClienteService {
    @Autowired
    TipoClienteRepository tipoClienteRepository;

    public TipoClienteService(TipoClienteRepository tipoClienteRepository) {
        this.tipoClienteRepository = tipoClienteRepository;
    }

    // Retorna todos los tipos de cliente
    public List<TipoClienteResponseDTO> findAll() {
        log.info("Obteniendo todos los tipos de cliente");
        return tipoClienteRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // Busca tipo de cliente por id, lanza 404 si no existe
    public TipoClienteResponseDTO findById(Long id) {
        log.info("Buscando tipo de cliente con id: {}", id);
        TipoCliente tipo = tipoClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cliente no encontrado con id: " + id));
        return toDTO(tipo);
    }

    public TipoClienteResponseDTO toDTO(TipoCliente tipo) {
        return new TipoClienteResponseDTO(
                tipo.getId(),
                tipo.getNombre(),
                //tipo.getDescripcion(),
                tipo.getDescuentoPct()
        );
    }

    // Retorna entidad directamente — usado por ClienteService
    public TipoCliente findEntityById(Long id) {
        return tipoClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cliente no encontrado con id: " + id));
    }
}