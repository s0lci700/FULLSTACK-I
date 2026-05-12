package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.ClienteSuscripcionCreateDTO;
import estacionamientos.user_service.dto.ClienteSuscripcionResponseDTO;
import estacionamientos.user_service.exception.ResourceNotFoundException;
import estacionamientos.user_service.model.Cliente;
import estacionamientos.user_service.model.ClienteSuscripcion;
import estacionamientos.user_service.model.Suscripcion;
import estacionamientos.user_service.repository.ClienteSuscripcionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteSuscripcionService {

    private static final Logger log = LoggerFactory.getLogger(ClienteSuscripcionService.class);

    private final ClienteSuscripcionRepository clienteSuscripcionRepository;
    private final ClienteService clienteService;
    private final SuscripcionService suscripcionService;

    public ClienteSuscripcionService(ClienteSuscripcionRepository clienteSuscripcionRepository,
                                     ClienteService clienteService,
                                     SuscripcionService suscripcionService) {
        this.clienteSuscripcionRepository = clienteSuscripcionRepository;
        this.clienteService = clienteService;
        this.suscripcionService = suscripcionService;
    }

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
                cs.getActivo()
        );
    }
}