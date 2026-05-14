package estacionamientos.ms_pagos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.ms_pagos.client.AccesoClient;
import estacionamientos.ms_pagos.client.ClienteClient;
import estacionamientos.ms_pagos.client.TarifaClient;
import estacionamientos.ms_pagos.dto.AccesoResponseDTO;
import estacionamientos.ms_pagos.dto.ClienteResponseDTO;
import estacionamientos.ms_pagos.dto.CobroCreateDTO;
import estacionamientos.ms_pagos.dto.CobroResponseDTO;
import estacionamientos.ms_pagos.dto.TarifaResponseDTO;
import estacionamientos.ms_pagos.exception.BusinessException;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.Cobro;
import estacionamientos.ms_pagos.model.MetodoPago;
import estacionamientos.ms_pagos.repository.CobroRepository;
import estacionamientos.ms_pagos.repository.MetodoPagoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PagoService {

    @Autowired
    CobroRepository cobroRepository;
    
    @Autowired
    MetodoPagoRepository metodoPagoRepository;

    @Autowired
    AccesoClient accesoClient;
    
    @Autowired
    TarifaClient tarifaClient;
    
    @Autowired
    ClienteClient clienteClient;

    // public PagoService(CobroRepository cobroRepository,
    //         MetodoPagoRepository metodoPagoRepository,
    //         AccesoClient accesoClient,
    //         TarifaClient tarifaClient,
    //         ClienteClient clienteClient) {
    //     this.cobroRepository = cobroRepository;
    //     this.metodoPagoRepository = metodoPagoRepository;
    //     this.accesoClient = accesoClient;
    //     this.tarifaClient = tarifaClient;
    //     this.clienteClient = clienteClient;
    // }

    // Genera un cobro consultando acceso, tarifa y cliente via Feign
    @Transactional
    public CobroResponseDTO crear(CobroCreateDTO dto) {
        log.info("Creando cobro para acceso id={}", dto.getIdAcceso());

        // Regla de negocio: no se puede cobrar dos veces el mismo acceso
        if (cobroRepository.findByIdAcceso(dto.getIdAcceso()).isPresent()) {
            throw new BusinessException("Ya existe un cobro para el acceso id=" + dto.getIdAcceso());
        }

        // Obtener datos remotos via Feign
        AccesoResponseDTO acceso = accesoClient.getById(dto.getIdAcceso());
        log.info("Acceso obtenido: idVehiculo={}, minutos={}", acceso.getIdVehiculo(), acceso.getMinutos());

        TarifaResponseDTO tarifa = tarifaClient.getTarifaVigente();
        log.info("Tarifa vigente: precioBaseHora={}", tarifa.getPrecioBaseHora());

        ClienteResponseDTO cliente = clienteClient.getById(dto.getIdCliente());
        log.info("Cliente obtenido: id={}", cliente.getId());

        // Validar metodo de pago
        MetodoPago metodoPago = metodoPagoRepository.findById(dto.getIdMetodoPago())
                .orElseThrow(
                        () -> new ResourceNotFoundException("MetodoPago no encontrado id=" + dto.getIdMetodoPago()));

        // Calcular descuento del banco si aplica
        double descuentoBanco = 0.0;
        if (metodoPago.getBanco() != null) {
            descuentoBanco = metodoPago.getBanco().getDescuento();
            log.info("Descuento banco aplicado: {}%", descuentoBanco);
        }

        // Obtener descuento por tipo de cliente
        double descuentoCliente = 0.0;
        if (cliente.getTipoCliente() != null) {
            descuentoCliente = cliente.getTipoCliente().getDescuentoPorcentaje();
            log.info("Descuento tipo cliente aplicado: {}%", descuentoCliente);
        }

        // Fórmula de cobro
        long minutos = acceso.getMinutos() != null ? acceso.getMinutos() : 0L;
        double montoBase = tarifa.getPrecioBaseHora() * (minutos / 60.0);
        double montoFinal = montoBase
                * (1 - descuentoCliente / 100)
                * (1 - descuentoBanco / 100);

        log.info("Calculo: minutos={}, montoBase={}, montoFinal={}", minutos, montoBase, montoFinal);

        // Guardar cobro
        Cobro cobro = new Cobro();
        cobro.setIdAcceso(dto.getIdAcceso());
        cobro.setIdCliente(dto.getIdCliente());
        cobro.setMinutos(minutos);
        cobro.setMontoBase(montoBase);
        cobro.setMontoFinal(montoFinal);
        cobro.setFechaCobro(LocalDateTime.now());
        cobro.setMetodoPago(metodoPago);

        Cobro guardado = cobroRepository.save(cobro);
        log.info("Cobro guardado id={}", guardado.getId());

        return toResponse(guardado);
    }

    // Busca un cobro por su ID
    public CobroResponseDTO findById(Long id) {
        log.info("Buscando cobro id={}", id);
        Cobro cobro = cobroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cobro no encontrado id=" + id));
        return toResponse(cobro);
    }

    // Busca un cobro por el ID del acceso
    public CobroResponseDTO findByIdAcceso(Long idAcceso) {
        log.info("Buscando cobro por acceso id={}", idAcceso);
        Cobro cobro = cobroRepository.findByIdAcceso(idAcceso)
                .orElseThrow(() -> new ResourceNotFoundException("Cobro no encontrado para acceso id=" + idAcceso));
        return toResponse(cobro);
    }

    // Retorna todos los cobros de un cliente
    public List<CobroResponseDTO> findByIdCliente(Long idCliente) {
        log.info("Buscando cobros del cliente id={}", idCliente);
        return cobroRepository.findAllByIdCliente(idCliente)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Convierte entidad Cobro a CobroResponseDTO
    private CobroResponseDTO toResponse(Cobro cobro) {
        CobroResponseDTO dto = new CobroResponseDTO();
        dto.setId(cobro.getId());
        dto.setIdAcceso(cobro.getIdAcceso());
        dto.setIdCliente(cobro.getIdCliente());
        dto.setMinutos(cobro.getMinutos());
        dto.setMontoBase(cobro.getMontoBase());
        dto.setMontoFinal(cobro.getMontoFinal());
        dto.setFechaCobro(cobro.getFechaCobro().toString());
        dto.setMetodoPago(cobro.getMetodoPago().getNombre());
        return dto;
    }
}