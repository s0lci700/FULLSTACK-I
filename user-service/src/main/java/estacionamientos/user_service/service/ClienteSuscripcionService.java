package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.ClienteSuscripcionCreateDTO;
import estacionamientos.user_service.dto.ClienteSuscripcionResponseDTO;
import estacionamientos.user_service.model.Cliente;
import estacionamientos.user_service.model.ClienteSuscripcion;
import estacionamientos.user_service.model.Suscripcion;
import estacionamientos.user_service.repository.ClienteSuscripcionRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClienteSuscripcionService {

    @Autowired
    ClienteSuscripcionRepository clienteSuscripcionRepository;

    @Autowired
    ClienteService clienteService;

    @Autowired
    SuscripcionService suscripcionService;


    // Retorna todas las suscripciones de un cliente especifico
    public List<ClienteSuscripcionResponseDTO> findByClienteId(Long clienteId) {
        log.info("Obteniendo suscripciones del cliente id: {}", clienteId);
        return clienteSuscripcionRepository.findByClienteId(clienteId).stream()
                .map(this::toDTO)
                .toList();
    }

    // Asigna una suscripcion a un cliente
    public ClienteSuscripcionResponseDTO create(Long clienteId, ClienteSuscripcionCreateDTO dto) {
        log.info("Asignando suscripcion al cliente id: {}", clienteId);
        if (clienteSuscripcionRepository.existsByClienteIdAndSuscripcionIdAndActivoTrue(
                clienteId, dto.getIdSuscripcion())) {
            throw new IllegalArgumentException(
                    "El cliente ya tiene esta suscripción activa");
        }
        Cliente cliente = clienteService.findEntityById(clienteId);
        Suscripcion suscripcion = suscripcionService.findEntityById(dto.getIdSuscripcion());
        ClienteSuscripcion cs = new ClienteSuscripcion();
        cs.setCliente(cliente);
        cs.setSuscripcion(suscripcion);
        cs.setFechaInicio(dto.getFechaInicio());
        cs.setFechaFin(dto.getFechaFin());
        cs.setActivo(true);
        ClienteSuscripcion guardado = clienteSuscripcionRepository.save(cs);
        log.info("Suscripcion asignada con id: {}", guardado.getId());
        return toDTO(guardado);
    }

    private ClienteSuscripcionResponseDTO toDTO(ClienteSuscripcion cs) {
        return new ClienteSuscripcionResponseDTO(
                cs.getId(),
                clienteService.toDTO(cs.getCliente()),
                suscripcionService.toDTO(cs.getSuscripcion()),
                cs.getFechaInicio(),
                cs.getFechaFin(),
                cs.getActivo());
    }
}