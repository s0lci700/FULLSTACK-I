package estacionamientos.ms_pagos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import estacionamientos.ms_pagos.dto.BancoDTO;
import estacionamientos.ms_pagos.dto.BancoResponseDTO;
import estacionamientos.ms_pagos.exception.BusinessException;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.Banco;
import estacionamientos.ms_pagos.repository.BancoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BancoService {

    private final BancoRepository bancoRepository;

    public BancoService(BancoRepository bancoRepository) {
        this.bancoRepository = bancoRepository;
    }

    public List<BancoResponseDTO> findAll() {
        log.info("Listando todos los bancos");
        return bancoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BancoResponseDTO findById(Long id) {
        log.info("Buscando banco id={}", id);
        Banco banco = bancoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco no encontrado id=" + id));
        return toResponse(banco);
    }

    @Transactional    
    public BancoResponseDTO create(BancoDTO dto) {
        log.info("Creando banco nombre={}", dto.getNombre());
        if (bancoRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe un banco con nombre=" + dto.getNombre());
        }
        Banco banco = new Banco();
        banco.setNombre(dto.getNombre());
        banco.setDescuentoPct(dto.getDescuento());
        return toResponse(bancoRepository.save(banco));
    }
    
    @Transactional
    public BancoResponseDTO update(Long id, BancoDTO dto) {
        log.info("Actualizando banco id={}", id);
        Banco banco = bancoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco no encontrado id=" + id));
        banco.setNombre(dto.getNombre());
        banco.setDescuentoPct(dto.getDescuento());
        return toResponse(bancoRepository.save(banco));
    }

    @Transactional
    public void delete(Long id) {
        log.info("Eliminando banco id={}", id);
        if (!bancoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banco no encontrado id=" + id);
        }
        bancoRepository.deleteById(id);
    }

    private BancoResponseDTO toResponse(Banco banco) {
        return new BancoResponseDTO(banco.getId(), banco.getNombre(), banco.getDescuentoPct());
    }
}
