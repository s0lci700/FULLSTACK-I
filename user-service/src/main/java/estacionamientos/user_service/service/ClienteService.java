package estacionamientos.user_service.service;

import estacionamientos.user_service.dto.ClienteCreateDTO;
import estacionamientos.user_service.dto.ClienteResponseDTO;
import estacionamientos.user_service.dto.ClienteUpdateDTO;
import estacionamientos.user_service.dto.TipoClienteResponseDTO;
import estacionamientos.user_service.exception.ConflictException;
import estacionamientos.user_service.exception.ResourceNotFoundException;
import estacionamientos.user_service.model.Cliente;
import estacionamientos.user_service.model.TipoCliente;
import estacionamientos.user_service.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClienteService {


    private final ClienteRepository clienteRepository;
    private final TipoClienteService tipoClienteService;

    public ClienteService(ClienteRepository clienteRepository,
            TipoClienteService tipoClienteService) {
        this.clienteRepository = clienteRepository;
        this.tipoClienteService = tipoClienteService;
    }

    // Retorna todos los clientes registrados
    public List<ClienteResponseDTO> findAll() {
        log.info("Obteniendo todos los clientes");
        return clienteRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // Busca cliente por id, lanza 404 si no existe
    public ClienteResponseDTO findById(Long id) {
        log.info("Buscando cliente con id: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return toDTO(cliente);
    }

    // Crea un nuevo cliente, valida que el email no este duplicado
    @Transactional
    public ClienteResponseDTO create(ClienteCreateDTO dto) {
        log.info("Creando cliente con email: {}", dto.getEmail());
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Ya existe un cliente con el email: " + dto.getEmail());
        }
        TipoCliente tipo = tipoClienteService.findEntityById(dto.getIdTipoCliente());
        Cliente cliente = new Cliente();
        cliente.setRut(dto.getRut());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setTipoCliente(tipo);
        cliente.setActivo(true);
        Cliente guardado = clienteRepository.save(cliente);
        log.info("Cliente creado con id: {}", guardado.getId());
        return toDTO(guardado);
    }

    // Actualiza nombre, apellido, telefono y tipo de cliente
    @Transactional
    public ClienteResponseDTO update(Long id, ClienteUpdateDTO dto) {
        log.info("Actualizando cliente con id: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        TipoCliente tipo = tipoClienteService.findEntityById(dto.getIdTipoCliente());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());
        cliente.setTipoCliente(tipo);
        Cliente actualizado = clienteRepository.save(cliente);
        log.info("Cliente actualizado con id: {}", actualizado.getId());
        return toDTO(actualizado);
    }

    // Eliminacion logica — marca activo=false
    @Transactional
    public void delete(Long id) {
        log.info("Desactivando cliente con id: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        cliente.setActivo(false);
        clienteRepository.save(cliente);
        log.info("Cliente desactivado con id: {}", id);
    }

    public ClienteResponseDTO toDTO(Cliente cliente) {
        TipoClienteResponseDTO tipoDTO = tipoClienteService.toDTO(cliente.getTipoCliente());
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getEmail(),
                cliente.getTelefono(),
                tipoDTO,
                cliente.getActivo());
    }

    // Retorna entidad directamente — usado por ClienteSuscripcionService
    public Cliente findEntityById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }
    
}