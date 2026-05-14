package estacionamientos.security_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import estacionamientos.security_service.dto.PermisoCreateDTO;
import estacionamientos.security_service.dto.PermisoResponseDTO;
import estacionamientos.security_service.exception.BusinessException;
import estacionamientos.security_service.exception.ResourceNotFoundException;
import estacionamientos.security_service.model.Permiso;
import estacionamientos.security_service.repository.PermisoRepository;

// Contiene toda la logica de negocio relacionada a permisos
// El Controller solo llama metodos de aqui, nunca accede al repositorio directamente
@Service
public class PermisoService {

    private static final Logger log = LoggerFactory.getLogger(PermisoService.class);

    // Inyeccion por constructor, mas segura que @Autowired
    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    // Retorna todos los permisos registrados en la base de datos
    public List<PermisoResponseDTO> findAll() {
        log.info("Listando todos los permisos");
        return permisoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Busca un permiso por ID, lanza 404 si no existe
    public PermisoResponseDTO findById(Long id) {
        log.info("Buscando permiso id={}", id);
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado id=" + id));
        return toResponse(permiso);
    }

    // Crea un nuevo permiso validando que el nombre no este duplicado
    // Si ya existe un permiso con ese nombre lanza BusinessException (400)
    public PermisoResponseDTO create(PermisoCreateDTO dto) {
        log.info("Creando permiso nombre={}", dto.getNombre());
        if (permisoRepository.existsByNombre(dto.getNombre())) {
            throw new BusinessException("Ya existe un permiso con nombre=" + dto.getNombre());
        }
        Permiso permiso = new Permiso();
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        return toResponse(permisoRepository.save(permiso));
    }

    // Actualiza nombre y descripcion de un permiso existente
    // Lanza 404 si el permiso no existe
    public PermisoResponseDTO update(Long id, PermisoCreateDTO dto) {
        log.info("Actualizando permiso id={}", id);
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado id=" + id));
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        return toResponse(permisoRepository.save(permiso));
    }

    // Elimina un permiso por ID
    // Lanza 404 si el permiso no existe
    public void delete(Long id) {
        log.info("Eliminando permiso id={}", id);
        if (!permisoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permiso no encontrado id=" + id);
        }
        permisoRepository.deleteById(id);
    }

    // Convierte la entidad Permiso al DTO de respuesta
    // Se usa en todos los metodos para no exponer la entidad directamente
    private PermisoResponseDTO toResponse(Permiso permiso) {
        return new PermisoResponseDTO(
                permiso.getId(),
                permiso.getNombre(),
                permiso.getDescripcion());
    }
}