package estacionamientos.ms_pagos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import estacionamientos.ms_pagos.dto.TipoTarjetaDTO;
import estacionamientos.ms_pagos.dto.TipoTarjetaResponseDTO;
import estacionamientos.ms_pagos.dto.TipoTarjetaUpdateDTO;
import estacionamientos.ms_pagos.exception.BusinessException;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.TipoTarjeta;
import estacionamientos.ms_pagos.repository.TipoTarjetaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TipoTarjetaService {

    

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

    @Transactional
    public TipoTarjetaResponseDTO create(TipoTarjetaDTO dto) {
        log.info("Creando tipo tarjeta nombre={}", dto.getNombre());
        if (tipoTarjetaRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe un tipo de tarjeta con nombre=" + dto.getNombre());
        }
        TipoTarjeta tipo = new TipoTarjeta();
        tipo.setNombre(dto.getNombre());
        return toResponse(tipoTarjetaRepository.save(tipo));
    }

    @Transactional
    public TipoTarjetaResponseDTO update(Long id, TipoTarjetaUpdateDTO dto) {
        log.info("Actualizando tipo tarjeta id={}", id);
        TipoTarjeta tipo = tipoTarjetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoTarjeta no encontrado id=" + id));
        if (!tipo.getNombre().equals(dto.getNombre()) && tipoTarjetaRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe un tipo de tarjeta con nombre=" + dto.getNombre());
        }
        tipo.setNombre(dto.getNombre());
        TipoTarjeta actualizado = tipoTarjetaRepository.save(tipo);
        log.info("Tipo tarjeta actualizado id={}", actualizado.getId());
        return toResponse(actualizado);
    }

    @Transactional
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