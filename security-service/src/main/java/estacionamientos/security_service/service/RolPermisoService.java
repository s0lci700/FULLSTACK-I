package estacionamientos.security_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import estacionamientos.security_service.dto.RolPermisoCreateDTO;
import estacionamientos.security_service.dto.RolPermisoResponseDTO;
import estacionamientos.security_service.exception.BusinessException;
import estacionamientos.security_service.exception.ResourceNotFoundException;
import estacionamientos.security_service.model.Permiso;
import estacionamientos.security_service.model.RolPermiso;
import estacionamientos.security_service.repository.PermisoRepository;
import estacionamientos.security_service.repository.RolPermisoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

// Contiene la logica de negocio para asignar permisos a roles
@Slf4j
@Service
public class RolPermisoService {



    private final RolPermisoRepository rolPermisoRepository;
    private final PermisoRepository permisoRepository;

    public RolPermisoService(RolPermisoRepository rolPermisoRepository,
                              PermisoRepository permisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.permisoRepository = permisoRepository;
    }

    // Retorna todas las asignaciones rol-permiso registradas
    public List<RolPermisoResponseDTO> findAll() {
        log.info("Listando todos los roles-permisos");
        return rolPermisoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Retorna todos los permisos asignados a un rol especifico
    // Util para saber que puede hacer un rol determinado
    public List<RolPermisoResponseDTO> findByIdRol(Long idRol) {
        log.info("Buscando permisos del rol id={}", idRol);
        return rolPermisoRepository.findAllByIdRol(idRol)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Asigna un permiso a un rol
    // Valida que el permiso exista y que no este ya asignado a ese rol
    @Transactional
    public RolPermisoResponseDTO create(RolPermisoCreateDTO dto) {
        log.info("Asignando permiso id={} al rol id={}", dto.getIdPermiso(), dto.getIdRol());

        // Verifica que el permiso existe en la base de datos
        Permiso permiso = permisoRepository.findById(dto.getIdPermiso())
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado id=" + dto.getIdPermiso()));

        // Regla de negocio: no se puede asignar el mismo permiso dos veces al mismo rol
        if (rolPermisoRepository.existsByIdRolAndPermisoId(dto.getIdRol(), dto.getIdPermiso())) {
            throw new BusinessException("El rol id=" + dto.getIdRol() +
                    " ya tiene asignado el permiso id=" + dto.getIdPermiso());
        }

        RolPermiso rolPermiso = new RolPermiso();
        rolPermiso.setIdRol(dto.getIdRol());
        rolPermiso.setPermiso(permiso);

        return toResponse(rolPermisoRepository.save(rolPermiso));
    }

    // Elimina una asignacion rol-permiso por ID
    // Lanza 404 si la asignacion no existe
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando rol-permiso id={}", id);
        if (!rolPermisoRepository.existsById(id)) {
            throw new ResourceNotFoundException("RolPermiso no encontrado id=" + id);
        }
        rolPermisoRepository.deleteById(id);
    }

    // Convierte la entidad RolPermiso al DTO de respuesta
    private RolPermisoResponseDTO toResponse(RolPermiso rp) {
        return new RolPermisoResponseDTO(
                rp.getId(),
                rp.getIdRol(),
                rp.getPermiso().getNombre(),
                rp.getPermiso().getDescripcion());
    }
}
