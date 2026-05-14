package estacionamientos.ms_pagos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import estacionamientos.ms_pagos.dto.MetodoPagoCreateDTO;
import estacionamientos.ms_pagos.dto.MetodoPagoResponseDTO;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.Banco;
import estacionamientos.ms_pagos.model.MetodoPago;
import estacionamientos.ms_pagos.model.TipoTarjeta;
import estacionamientos.ms_pagos.repository.BancoRepository;
import estacionamientos.ms_pagos.repository.MetodoPagoRepository;
import estacionamientos.ms_pagos.repository.TipoTarjetaRepository;

@Service
public class MetodoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MetodoPagoService.class);

    private final MetodoPagoRepository metodoPagoRepository;
    private final BancoRepository bancoRepository;
    private final TipoTarjetaRepository tipoTarjetaRepository;

    public MetodoPagoService(MetodoPagoRepository metodoPagoRepository,
                              BancoRepository bancoRepository,
                              TipoTarjetaRepository tipoTarjetaRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.bancoRepository = bancoRepository;
        this.tipoTarjetaRepository = tipoTarjetaRepository;
    }

    public List<MetodoPagoResponseDTO> findAll() {
        log.info("Listando todos los metodos de pago");
        return metodoPagoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MetodoPagoResponseDTO findById(Long id) {
        log.info("Buscando metodo de pago id={}", id);
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MetodoPago no encontrado id=" + id));
        return toResponse(metodo);
    }

    public MetodoPagoResponseDTO create(MetodoPagoCreateDTO dto) {
        log.info("Creando metodo de pago nombre={}", dto.getNombre());
        MetodoPago metodo = new MetodoPago();
        metodo.setNombre(dto.getNombre());

        if (dto.getIdBanco() != null) {
            Banco banco = bancoRepository.findById(dto.getIdBanco())
                    .orElseThrow(() -> new ResourceNotFoundException("Banco no encontrado id=" + dto.getIdBanco()));
            metodo.setBanco(banco);
        }

        if (dto.getIdTipoTarjeta() != null) {
            TipoTarjeta tipo = tipoTarjetaRepository.findById(dto.getIdTipoTarjeta())
                    .orElseThrow(() -> new ResourceNotFoundException("TipoTarjeta no encontrado id=" + dto.getIdTipoTarjeta()));
            metodo.setTipoTarjeta(tipo);
        }

        return toResponse(metodoPagoRepository.save(metodo));
    }

    public void delete(Long id) {
        log.info("Eliminando metodo de pago id={}", id);
        if (!metodoPagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("MetodoPago no encontrado id=" + id);
        }
        metodoPagoRepository.deleteById(id);
    }

    private MetodoPagoResponseDTO toResponse(MetodoPago metodo) {
        String banco = metodo.getBanco() != null ? metodo.getBanco().getNombre() : null;
        String tipoTarjeta = metodo.getTipoTarjeta() != null ? metodo.getTipoTarjeta().getNombre() : null;
        return new MetodoPagoResponseDTO(metodo.getId(), metodo.getNombre(), banco, tipoTarjeta);
    }
}