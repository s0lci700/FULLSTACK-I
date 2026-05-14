package estacionamientos.ms_pagos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import estacionamientos.ms_pagos.dto.TipoTarjetaDTO;
import estacionamientos.ms_pagos.dto.TipoTarjetaResponseDTO;
import estacionamientos.ms_pagos.exception.BusinessException;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.TipoTarjeta;
import estacionamientos.ms_pagos.repository.TipoTarjetaRepository;

@Service
public class TipoTarjetaService {

    private static final Logger log = LoggerFactory.getLogger(TipoTarjetaService.class);

    private final TipoTarjetaRepository tipoTarjetaRepository;

    public TipoTarjetaService(TipoTarjetaRepository tipoTarjetaRepository) {
        this.tipoTarjetaRepository = tipoTarjetaRepository;
    }

    public List<TipoTarjetaResponseDTO> findAll() {
        log.info("Listando todos los tipos de tarjeta");
        return tipoTarjetaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TipoTarjetaResponseDTO findById(Long id) {
        log.info("Buscando tipo tarjeta id={}", id);
        TipoTarjeta tipo = tipoTarjetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoTarjeta no encontrado id=" + id));
        return toResponse(tipo);
    }

    public TipoTarjetaResponseDTO create(TipoTarjetaDTO dto) {
        log.info("Creando tipo tarjeta nombre={}", dto.getNombre());
        if (tipoTarjetaRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe un tipo de tarjeta con nombre=" + dto.getNombre());
        }
        TipoTarjeta tipo = new TipoTarjeta();
        tipo.setNombre(dto.getNombre());
        return toResponse(tipoTarjetaRepository.save(tipo));
    }

    public void delete(Long id) {
        log.info("Eliminando tipo tarjeta id={}", id);
        if (!tipoTarjetaRepository.existsById(id)) {
            throw new ResourceNotFoundException("TipoTarjeta no encontrado id=" + id);
        }
        tipoTarjetaRepository.deleteById(id);
    }

    private TipoTarjetaResponseDTO toResponse(TipoTarjeta tipo) {
        return new TipoTarjetaResponseDTO(tipo.getId(), tipo.getNombre());
    }
}