package estacionamientos.ms_accesos.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.ms_accesos.client.EspacioClient;
import estacionamientos.ms_accesos.client.ReservaClient;
import estacionamientos.ms_accesos.dto.AccesoCreateDTO;
import estacionamientos.ms_accesos.dto.AccesoResponseDTO;
import estacionamientos.ms_accesos.dto.ReservaResponseDTO;
import estacionamientos.ms_accesos.exception.ConflictException;
import estacionamientos.ms_accesos.exception.NotFoundException;
import estacionamientos.ms_accesos.model.Acceso;
import estacionamientos.ms_accesos.model.EstadoEnum;
import estacionamientos.ms_accesos.repository.AccesoRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccesoService {
    @Autowired
    AccesoRepository accesoRepository;

    @Autowired
    EspacioClient espacioClient;

    @Autowired
    ReservaClient reservaClient;

@Transactional
public AccesoResponseDTO registrarEntrada(AccesoCreateDTO dto) {

    //Feign nunca retorna null — capturar excepción correctamente
    ReservaResponseDTO reserva;
    try {
        reserva = reservaClient.findById(dto.getIdReserva());
    } catch (FeignException.NotFound e) {
        log.warn("ConflictException: Reserva no encontrada con id: {}", dto.getIdReserva());
        throw new ConflictException("Reserva no encontrada con id: " + dto.getIdReserva());
    } catch (FeignException e) {
        log.warn("ConflictException: Error al consultar la reserva: {}", e.getMessage());
        throw new ConflictException("Error al consultar la reserva: " + e.getMessage());
    }

    //Ahora esta validación sí tiene sentido (reserva nunca es null aquí)
    if (!reserva.getEstado().equals("CONFIRMADA")) {
        log.warn("ConflictException: Reserva no válida para registrar entrada, idReserva={}, estado={}",
                dto.getIdReserva(), reserva.getEstado());
        throw new ConflictException("Reserva no válida para registrar entrada");
    }

    accesoRepository.findByIdReserva(dto.getIdReserva()).ifPresent(
            acceso -> {
                log.warn("ConflictException: Ya existe un acceso registrado para esta reserva, idReserva={}",
                        dto.getIdReserva());
                throw new ConflictException("Ya existe un acceso registrado para esta reserva");
            });

    Acceso acceso = new Acceso();
    acceso.setIdReserva(dto.getIdReserva());
    acceso.setIdEspacio(reserva.getIdEspacio());
    acceso.setIdVehiculo(dto.getIdVehiculo());
    acceso.setPatenteEscaneada(dto.getPatenteEscaneada());
    acceso.setFechaHoraEntrada(LocalDateTime.now());
    acceso.setEstado(EstadoEnum.ACTIVO);

    //También proteger la llamada a espacioClient
    try {
        espacioClient.updateDisponibilidad(reserva.getIdEspacio(), false);
    } catch (FeignException e) {
        log.warn("ConflictException: Error al actualizar disponibilidad del espacio: {}", e.getMessage());
        throw new ConflictException("Error al actualizar disponibilidad del espacio: " + e.getMessage());
    }

    log.info("Entrada registrada: idEspacio={}, idReserva={}, idVehiculo={}",
            reserva.getIdEspacio(), dto.getIdReserva(), dto.getIdVehiculo());
    return toDTO(accesoRepository.save(acceso));
}

    @Transactional
    public AccesoResponseDTO registrarSalida(Long id) {
        Acceso acceso = accesoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Acceso no encontrado"));

        if (acceso.getFechaHoraSalida() != null) {
            log.warn("ConflictException: Salida ya registrada para este acceso, id={}", id);
            throw new ConflictException("Salida ya registrada para este acceso");
        }

        LocalDateTime ahora = LocalDateTime.now();
        long minutos = ChronoUnit.MINUTES.between(acceso.getFechaHoraEntrada(), ahora);

        acceso.setFechaHoraSalida(ahora);
        acceso.setMinutos((int) minutos);
        acceso.setEstado(EstadoEnum.COMPLETADO);

        try {
            espacioClient.updateDisponibilidad(acceso.getIdEspacio(), true);
            reservaClient.finalizarReserva(acceso.getIdReserva());
        } catch (FeignException e) {
            log.warn("ConflictException: Error al liberar espacio o finalizar reserva: {}", e.getMessage());
            throw new ConflictException("Error al liberar espacio o finalizar reserva: " + e.getMessage());
        }

        log.info("Salida registrada: id={}, idEspacio={}, idReserva={}, minutos={}",
                id, acceso.getIdEspacio(), acceso.getIdReserva(), minutos);
        return toDTO(accesoRepository.save(acceso));
    }

    public List<AccesoResponseDTO> findAll() {
        log.info("Obteniendo todos los accesos");
        return accesoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public AccesoResponseDTO findById(Long id) {
        Acceso acceso = accesoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Acceso no encontrado id=" + id));
        return toDTO(acceso);
    }

    public AccesoResponseDTO findByReserva(Long idReserva) {
        Acceso acceso = accesoRepository.findByIdReserva(idReserva)
                .orElseThrow(() -> new NotFoundException("Acceso no encontrado para la reserva: " + idReserva));
        return toDTO(acceso);
    }

    public AccesoResponseDTO toDTO(Acceso acceso) {
        AccesoResponseDTO dto = new AccesoResponseDTO();
        dto.setId(acceso.getId());
        dto.setIdVehiculo(acceso.getIdVehiculo());
        dto.setIdReserva(acceso.getIdReserva());
        dto.setIdEspacio(acceso.getIdEspacio());
        dto.setPatenteEscaneada(acceso.getPatenteEscaneada());
        dto.setFechaHoraEntrada(acceso.getFechaHoraEntrada());
        dto.setFechaHoraSalida(acceso.getFechaHoraSalida());
        dto.setEstado(acceso.getEstado().name());

        dto.setMinutos(acceso.getMinutos() != null ? acceso.getMinutos().longValue() : null);

        // Mapear campos de Acceso a AccesoResponseDTO
        return dto;
    }
}
