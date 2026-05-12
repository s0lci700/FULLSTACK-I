package estacionamientos.ms_vehiculos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.ms_vehiculos.exception.NotFoundException;
import estacionamientos.ms_vehiculos.model.Vehiculo;
import estacionamientos.ms_vehiculos.repository.VehiculoRepository;
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

    public List<Vehiculo> listarTodos() {
        if (vehiculoRepository.findAll().isEmpty()) {
            throw new NotFoundException("El repositorio esta vacio");
        }
        return vehiculoRepository.findAll();
    }

    public Vehiculo obtenerPorId(Long id) {
        return vehiculoRepository.findById(id);
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
