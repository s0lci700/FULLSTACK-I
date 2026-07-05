package estacionamientos.ms_pagos.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.ms_pagos.client.AccesoClient;
import estacionamientos.ms_pagos.client.ClienteClient;
import estacionamientos.ms_pagos.client.EspacioClient;
import estacionamientos.ms_pagos.client.HorarioTarifaClient;
import estacionamientos.ms_pagos.client.TarifaClient;
import estacionamientos.ms_pagos.client.TipoVehiculoClient;
import estacionamientos.ms_pagos.client.VehiculoClient;
import estacionamientos.ms_pagos.dto.AccesoResponseDTO;
import estacionamientos.ms_pagos.dto.ClienteResponseDTO;
import estacionamientos.ms_pagos.dto.CobroCreateDTO;
import estacionamientos.ms_pagos.dto.CobroResponseDTO;
import estacionamientos.ms_pagos.dto.EspacioResponseDTO;
import estacionamientos.ms_pagos.dto.HorarioTarifaResponseDTO;
import estacionamientos.ms_pagos.dto.TarifaResponseDTO;
import estacionamientos.ms_pagos.dto.VehiculoResponseDTO;
import estacionamientos.ms_pagos.exception.BusinessException;
import estacionamientos.ms_pagos.exception.ResourceNotFoundException;
import estacionamientos.ms_pagos.model.Cobro;
import estacionamientos.ms_pagos.model.EstadoCobroEnum;
import estacionamientos.ms_pagos.model.MetodoPago;
import estacionamientos.ms_pagos.repository.CobroRepository;
import estacionamientos.ms_pagos.repository.MetodoPagoRepository;
import feign.FeignException;
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
    VehiculoClient vehiculoClient;
    @Autowired
    TipoVehiculoClient tipoVehiculoClient;
    @Autowired
    EspacioClient espacioClient;
    @Autowired
    HorarioTarifaClient horarioTarifaClient;
    @Autowired
    TarifaClient tarifaClient;
    @Autowired
    ClienteClient clienteClient;

    // ── Public API ───────────────────────────────────────────────────────────

    @Transactional
    public CobroResponseDTO crear(CobroCreateDTO dto) {
        log.info("Creando cobro para acceso id={}", dto.getIdAcceso());

        validarSinCobroDuplicado(dto.getIdAcceso());

        AccesoResponseDTO acceso = fetchAcceso(dto.getIdAcceso());
        TarifaResponseDTO tarifa = fetchTarifaVigente();
        MetodoPago metodoPago = fetchMetodoPago(dto.getIdMetodoPago());
        ClienteResponseDTO cliente = fetchCliente(dto.getIdCliente());

        BigDecimal factorVehiculo = resolverFactorVehiculo(acceso.getIdVehiculo());
        BigDecimal factorEspacio = resolverFactorEspacio(acceso.getIdEspacio());
        BigDecimal multiplicadorHorario = resolverMultiplicadorHorario();

        BigDecimal descBanco = resolverDescuentoBanco(metodoPago);
        BigDecimal descCliente = resolverDescuentoCliente(cliente);
        BigDecimal descSuscripcion = cliente.getSuscripcion() != null ? cliente.getSuscripcion().getDescuentoPct() : BigDecimal.ZERO;
        Integer minutos = acceso.getMinutos() != null ? acceso.getMinutos() : 0;
        BigDecimal montoBase = calcularMontoBase(tarifa, factorVehiculo, factorEspacio, multiplicadorHorario, minutos);
        BigDecimal montoFinal = calcularMontoFinal(montoBase, descCliente, descSuscripcion, descBanco);

        log.info(
                "Calculo: minutos={}, factorVehiculo={}, factorEspacio={}, multiplicadorHorario={}, montoBase={}, montoFinal={}",
                minutos, factorVehiculo, factorEspacio, multiplicadorHorario, montoBase, montoFinal);

        Cobro guardado = cobroRepository.save(
                buildCobro(
                    dto, 
                    tarifa, 
                    metodoPago, 
                    minutos, 
                    montoBase, 
                    descCliente, 
                    descSuscripcion, 
                    descBanco,
                    montoFinal));
        log.info("Cobro guardado id={}", guardado.getId());
        return toResponse(guardado);
    }

    public CobroResponseDTO findById(Long id) {
        log.info("Buscando cobro id={}", id);
        Cobro cobro = cobroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cobro no encontrado id=" + id));
        return toResponse(cobro);
    }

    public CobroResponseDTO findByIdAcceso(Long idAcceso) {
        log.info("Buscando cobro por acceso id={}", idAcceso);
        Cobro cobro = cobroRepository.findByIdAcceso(idAcceso)
                .orElseThrow(() -> new ResourceNotFoundException("Cobro no encontrado para acceso id=" + idAcceso));
        return toResponse(cobro);
    }

    public List<CobroResponseDTO> findAll() {
        log.info("Obteniendo todos los cobros");
        return cobroRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CobroResponseDTO> findByIdCliente(Long idCliente) {
        log.info("Buscando cobros del cliente id={}", idCliente);
        return cobroRepository.findAllByMetodoPagoIdClienteRef(idCliente)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Validation ───────────────────────────────────────────────────────────

    private void validarSinCobroDuplicado(Long idAcceso) {
        if (cobroRepository.findByIdAcceso(idAcceso).isPresent()) {
            throw new BusinessException("Ya existe un cobro para el acceso id=" + idAcceso);
        }
    }

    // ── Data fetching ────────────────────────────────────────────────────────

    private MetodoPago fetchMetodoPago(Long id) {
        return metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MetodoPago no encontrado id=" + id));
    }

    private AccesoResponseDTO fetchAcceso(Long idAcceso) {
        try {
            return accesoClient.getById(idAcceso);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Acceso no encontrado id=" + idAcceso);
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-accesos (status {}): {}", e.status(), e.getMessage());
            throw new BusinessException("No se pudo obtener el acceso: servicio no disponible");
        }
    }

    private TarifaResponseDTO fetchTarifaVigente() {
        try {
            return tarifaClient.getTarifaVigente();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("No hay tarifa vigente configurada");
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-tarifas (status {}): {}", e.status(), e.getMessage());
            throw new BusinessException("No se pudo obtener la tarifa vigente: servicio no disponible");
        }
    }

    private ClienteResponseDTO fetchCliente(Long idCliente) {
        try {
            return clienteClient.getById(idCliente);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Cliente no encontrado id=" + idCliente);
        } catch (FeignException e) {
            log.error("Error de comunicacion con user-service (status {}): {}", e.status(), e.getMessage());
            throw new BusinessException("No se pudo obtener el cliente: servicio no disponible");
        }
    }

    private BigDecimal resolverFactorVehiculo(Long idVehiculo) {
        Float factor;
        try {
            VehiculoResponseDTO vehiculo = vehiculoClient.getById(idVehiculo);
            if (vehiculo.getIdTipoVehiculo() == null) {
                return BigDecimal.ONE;
            }
            factor = tipoVehiculoClient.getById(vehiculo.getIdTipoVehiculo()).getFactorPrecio();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Vehiculo o tipo de vehiculo no encontrado para id=" + idVehiculo);
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-vehiculos (status {}): {}", e.status(), e.getMessage());
            throw new BusinessException("No se pudo obtener el factor de vehiculo: servicio no disponible");
        }
        BigDecimal result = BigDecimal.valueOf(factor.doubleValue());
        log.info("Factor tipo vehiculo: {}", result);
        return result;
    }

    private BigDecimal resolverFactorEspacio(Long idEspacio) {
        Float factor;
        try {
            EspacioResponseDTO espacio = espacioClient.getById(idEspacio);
            if (espacio.getTipoEspacio() == null) {
                return BigDecimal.ONE;
            }
            factor = espacio.getTipoEspacio().getFactorPrecio();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Espacio no encontrado id=" + idEspacio);
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-espacios (status {}): {}", e.status(), e.getMessage());
            throw new BusinessException("No se pudo obtener el factor de espacio: servicio no disponible");
        }
        BigDecimal result = BigDecimal.valueOf(factor.doubleValue());
        log.info("Factor tipo espacio: {}", result);
        return result;
    }

    private BigDecimal resolverMultiplicadorHorario() {
        try {
            HorarioTarifaResponseDTO horario = horarioTarifaClient.getVigente();
            if (horario != null && horario.getMultiplicador() != null) {
                BigDecimal mult = BigDecimal.valueOf(horario.getMultiplicador().doubleValue());
                log.info("Multiplicador horario aplicado: {}", mult);
                return mult;
            }
            log.warn("Horario vigente sin multiplicador definido, usando 1.0");
        } catch (FeignException.NotFound e) {
            log.warn("No hay horario vigente configurado, usando multiplicador 1.0");
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-tarifas (status {}): {}", e.status(), e.getMessage());
        }
        return BigDecimal.ONE;
    }

    // ── Discount resolution ──────────────────────────────────────────────────

    private BigDecimal resolverDescuentoBanco(MetodoPago metodoPago) {
        if (metodoPago.getBanco() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal desc = metodoPago.getBanco().getDescuentoPct();
        log.info("Descuento banco aplicado: {}%", desc);
        return desc;
    }

    private BigDecimal resolverDescuentoCliente(ClienteResponseDTO cliente) {
        if (cliente.getTipoCliente() == null || cliente.getTipoCliente().getDescuentoPct() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal desc = cliente.getTipoCliente().getDescuentoPct();
        log.info("Descuento tipo cliente aplicado: {}%", desc);
        return desc;
    }

    // ── Cobro formula ──────────────────────────────────────────────────────

    private BigDecimal calcularMontoBase(TarifaResponseDTO tarifa,
            BigDecimal factorVehiculo,
            BigDecimal factorEspacio,
            BigDecimal multiplicadorHorario,
            int minutos) {
        BigDecimal horas = BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(tarifa.getPrecioBaseHora().doubleValue())
                .multiply(multiplicadorHorario)
                .multiply(factorVehiculo)
                .multiply(factorEspacio)
                .multiply(horas)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularMontoFinal(BigDecimal montoBase,
            BigDecimal descCliente,
            BigDecimal descSuscripcion,
            BigDecimal descBanco) {
        BigDecimal cien = BigDecimal.valueOf(100);
        BigDecimal factorCliente = BigDecimal.ONE.subtract(descCliente.divide(cien, 4, RoundingMode.HALF_UP));
        BigDecimal factorSuscripcion = BigDecimal.ONE.subtract(descSuscripcion.divide(cien, 4, RoundingMode.HALF_UP));
        BigDecimal factorBanco = BigDecimal.ONE.subtract(descBanco.divide(cien, 4, RoundingMode.HALF_UP));
        return montoBase
                .multiply(factorCliente)
                .multiply(factorSuscripcion)
                .multiply(factorBanco)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ── Entity construction ──────────────────────────────────────────────────

    private Cobro buildCobro(CobroCreateDTO dto,
            TarifaResponseDTO tarifa,
            MetodoPago metodoPago,
            int minutos,
            BigDecimal montoBase,
            BigDecimal descCliente,
            BigDecimal descSuscripcion,
            BigDecimal descBanco,
            BigDecimal montoFinal) {
        Cobro cobro = new Cobro();
        cobro.setIdAcceso(dto.getIdAcceso());
        cobro.setMetodoPago(metodoPago);
        cobro.setIdTarifaRef(tarifa.getId());
        cobro.setMinutos(minutos);
        cobro.setMontoBase(montoBase);
        cobro.setDescTipoCliente(descCliente);
        cobro.setDescSuscripcion(descSuscripcion);
        cobro.setDescBanco(descBanco);
        cobro.setMontoFinal(montoFinal);
        cobro.setEstado(EstadoCobroEnum.PENDIENTE);
        cobro.setFechaCobro(LocalDateTime.now());
        return cobro;
    }

    // ── DTO mapping ──────────────────────────────────────────────────────────

    private CobroResponseDTO toResponse(Cobro cobro) {
        CobroResponseDTO dto = new CobroResponseDTO();
        dto.setId(cobro.getId());
        dto.setIdAcceso(cobro.getIdAcceso());
        dto.setIdCliente(cobro.getMetodoPago().getIdClienteRef());
        dto.setMinutos(cobro.getMinutos());
        dto.setMontoBase(cobro.getMontoBase().floatValue());
        dto.setMontoFinal(cobro.getMontoFinal().doubleValue());
        dto.setFechaCobro(cobro.getFechaCobro());
        dto.setMetodoPago(cobro.getMetodoPago().getTipoTarjeta() != null
                ? cobro.getMetodoPago().getTipoTarjeta().getNombre()
                : "N/A");
        return dto;
    }
}
