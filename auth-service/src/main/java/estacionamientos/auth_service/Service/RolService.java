package estacionamientos.auth_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.auth_service.repository.RolRepository;
import estacionamientos.auth_service.dto.RolCreateDTO;
import estacionamientos.auth_service.dto.RolResponseDTO;
import estacionamientos.auth_service.dto.RolUpdateDTO;
import estacionamientos.auth_service.exception.ConflictException;
import estacionamientos.auth_service.exception.NotFoundException;
import estacionamientos.auth_service.model.Rol;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RolService {

    @Autowired
    RolRepository rolRepository;

    public List<RolResponseDTO> findAll() {
        log.info("Listando todos los roles");
        return rolRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public RolResponseDTO findById(Long id) {
        log.info("Buscando rol con id: {}", id);
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con id: " + id));
        return toDTO(rol);
    }

    @Transactional
    public RolResponseDTO create(RolCreateDTO dto) {
        log.info("Creando rol nombre={}", dto.getNombre());
        if (rolRepository.existsByNombre(dto.getNombre())) {
            throw new ConflictException("Ya existe un rol con el nombre: " + dto.getNombre());
        }
        Rol rol = new Rol();
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        Rol guardado = rolRepository.save(rol);
        log.info("Rol creado con id: {}", guardado.getId());
        return toDTO(guardado);
    }

    @Transactional
    public RolResponseDTO update(Long id, RolUpdateDTO dto) {
        log.info("Actualizando rol con id: {}", id);
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado con id: " + id));
        if (!rol.getNombre().equals(dto.getNombre()) && rolRepository.existsByNombre(dto.getNombre())) {
            throw new ConflictException("Ya existe un rol con el nombre: " + dto.getNombre());
        }
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        Rol actualizado = rolRepository.save(rol);
        log.info("Rol actualizado con id: {}", actualizado.getId());
        return toDTO(actualizado);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Eliminando rol con id: {}", id);
        if (!rolRepository.existsById(id)) {
            throw new NotFoundException("Rol no encontrado con id: " + id);
        }
        rolRepository.deleteById(id);
        log.info("Rol eliminado con id: {}", id);
    }

    private RolResponseDTO toDTO(Rol rol) {
        return new RolResponseDTO(rol.getId(), rol.getNombre(), rol.getDescripcion());
    }
}
