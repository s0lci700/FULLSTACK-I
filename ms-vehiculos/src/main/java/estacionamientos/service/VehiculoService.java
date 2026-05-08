package estacionamientos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.model.Vehiculo;
import estacionamientos.repository.VehiculoRepository;
import jakarta.transaction.Transactional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    /**
     * @Transactional asegura que si algo falla, los cambios en la BD se reviertan.
     * Logica y reglas de negocio
     */

    @Transactional
    public boolean exists(String patente) {
        return vehiculoRepository.findByPatente(patente).isPresent();
    }

    // TODO: Implementar los métodos CRUD del servicio:
    // - listarTodos()              → vehiculoRepository.findAll()
    // - obtenerPorId(Long id)      → buscar o lanzar NotFoundException
    // - crear(VehiculoCreateDTO)   → validar que la patente no exista, guardar
    // - actualizar(Long, VehiculoUpdateDTO) → buscar, aplicar cambios, guardar
    // - eliminar(Long id)          → buscar o lanzar NotFoundException, setActivo(false)

    // TODO: Agregar VehiculoRepository y métodos para TipoVehiculo,
    // o crear un TipoVehiculoService separado que maneje su propio CRUD.

    // TODO: Crear las clases de excepción en el paquete exception/:
    // NotFoundException, ConflictException, BadRequestException, BusinessException
    // y el GlobalExceptionHandler con @RestControllerAdvice.

}
