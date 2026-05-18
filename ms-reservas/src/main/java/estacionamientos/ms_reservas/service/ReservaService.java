package estacionamientos.ms_reservas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.ms_reservas.client.ClienteClient;
import estacionamientos.ms_reservas.client.EspacioClient;
import estacionamientos.ms_reservas.client.VehiculoClient;
import estacionamientos.ms_reservas.dto.ClienteResponseDTO;
import estacionamientos.ms_reservas.dto.EspacioResponseDTO;
import estacionamientos.ms_reservas.dto.ReservaCreateDTO;
import estacionamientos.ms_reservas.dto.ReservaResponseDTO;
import estacionamientos.ms_reservas.dto.VehiculoResponseDTO;
import estacionamientos.ms_reservas.exception.NotFoundException;
import estacionamientos.ms_reservas.model.EstadoEnums;
import estacionamientos.ms_reservas.model.Reserva;
import estacionamientos.ms_reservas.repository.ReservaRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservasRepository;

    @Autowired
    private ClienteClient clienteClient;

    @Autowired
    private VehiculoClient vehiculoClient;

    @Autowired
    private EspacioClient espacioClient;

    public List<ReservaResponseDTO> findAll() {
        return reservasRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ReservaResponseDTO findById(Long id) {
        Reserva reserva = reservasRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        return toDTO(reserva);
    }

    public List<ReservaResponseDTO> findByIdCliente(Long idCliente) {
        return reservasRepository.findByIdCliente(idCliente).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ReservaResponseDTO> findByEstado(EstadoEnums estado) {
        log.info("Buscando reservas por estado: {}", estado);
        return reservasRepository.findByEstado(estado).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public ReservaResponseDTO create(ReservaCreateDTO reserva) {

        // clienteClient
        ClienteResponseDTO cliente;
        try {
            cliente = clienteClient.findById(reserva.getIdCliente());
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Cliente no encontrado con id: " + reserva.getIdCliente());
        } catch (FeignException e) {
            throw new NotFoundException("Error al consultar el cliente: " + e.getMessage());
        }

        if (!cliente.getActivo()) {
            throw new NotFoundException("Cliente no encontrado o inactivo");
        }

        // vehiculoClient
        VehiculoResponseDTO vehiculo;
        try {
            vehiculo = vehiculoClient.findById(reserva.getIdVehiculo());
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Vehículo no encontrado con id: " + reserva.getIdVehiculo());
        } catch (FeignException e) {
            throw new NotFoundException("Error al consultar el vehículo: " + e.getMessage());
        }

        if (!vehiculo.getActivo()) {
            throw new NotFoundException("Vehículo no encontrado o inactivo");
        }

        // espacioClient
        EspacioResponseDTO espacio;
        try {
            espacio = espacioClient.findById(reserva.getIdEspacio());
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Espacio no encontrado con id: " + reserva.getIdEspacio());
        } catch (FeignException e) {
            throw new NotFoundException("Error al consultar el espacio: " + e.getMessage());
        }

        if (!espacio.getActivo() || !espacio.getDisponible()) {
            throw new NotFoundException("Espacio no encontrado, inactivo o no disponible");
        }

        Reserva nReserva = new Reserva();
        nReserva.setIdCliente(reserva.getIdCliente());
        nReserva.setIdVehiculo(reserva.getIdVehiculo());
        nReserva.setIdEspacio(reserva.getIdEspacio());
        nReserva.setFechaInicio(reserva.getFechaInicio());
        nReserva.setFechaFin(reserva.getFechaFin());
        nReserva.setEstado(EstadoEnums.PENDIENTE);

        return toDTO(reservasRepository.save(nReserva));
    }

    @Transactional
    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = reservasRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        reserva.setEstado(EstadoEnums.CANCELADA);
        return toDTO(reservasRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO confirmar(Long id) {
        Reserva reserva = reservasRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        if (reserva.getEstado() != EstadoEnums.CONFIRMADA) {
            reserva.setEstado(EstadoEnums.CONFIRMADA);
        }
        return toDTO(reservasRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO finalizar(Long id) {
        Reserva reserva = reservasRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        if (reserva.getEstado() != EstadoEnums.FINALIZADA) {
            reserva.setEstado(EstadoEnums.FINALIZADA);
        }
        return toDTO(reservasRepository.save(reserva));
    }

    private ReservaResponseDTO toDTO(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getIdCliente(),
                reserva.getIdVehiculo(),
                reserva.getIdEspacio(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getEstado(),
                reserva.getFechaCreacion());
    }
}
