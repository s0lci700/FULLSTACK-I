package estacionamientos.ms_vehiculos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.ms_vehiculos.dto.VehiculoCreateDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoUpdateDTO;
import estacionamientos.ms_vehiculos.exception.AlreadyFoundException;
import estacionamientos.ms_vehiculos.exception.NotFoundException;
import estacionamientos.ms_vehiculos.model.TipoVehiculo;
import estacionamientos.ms_vehiculos.model.Vehiculo;
import estacionamientos.ms_vehiculos.repository.TipoVehiculoRepository;
import estacionamientos.ms_vehiculos.repository.VehiculoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;


    /**
     * @Transactional asegura que si algo falla, los cambios en la BD se reviertan.
     * Logica y reglas de negocio
     */

    @Transactional
    public boolean exists(String patente) {
        return vehiculoRepository.findByPatente(patente).isPresent();
    }

    public List<Vehiculo> listarTodos() {
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        if (vehiculos.isEmpty()) {
            throw new NotFoundException("El repositorio esta vacio");
        }
        return vehiculos;
    }

    public Vehiculo obtenerPorId(Long id) {
        return vehiculoRepository.findById(id).orElseThrow(() -> new NotFoundException("Vehiculo no encontrado"));
    }

    @Transactional
    public void crear(VehiculoCreateDTO dto) {
        if (vehiculoRepository.findByPatente(dto.patente).isPresent()) {
            throw new AlreadyFoundException("Ya se encuentra la patente");
        }
        Vehiculo vehiculo = new Vehiculo();
        TipoVehiculo tipoVehiculo = tipoVehiculoRepository
            .findById(dto.getIdTipoVehiculo())
            .orElseThrow(() -> new NotFoundException("Tipo de vehiculo no encontrado"));
        vehiculo.setPatente(dto.getPatente());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setColor(dto.getColor());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setIdTipoVehiculo(tipoVehiculo);
        vehiculo.setIdClienteRef(dto.getIdClienteRef());
        vehiculoRepository.save(vehiculo);
        
    }

    @Transactional
    public void actualizar(Long id, VehiculoUpdateDTO dto) {
        Vehiculo vehiculo = vehiculoRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Vehiculo no encontrado"));
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setColor(dto.getColor());
        vehiculo.setAnio(dto.getAnio());
        TipoVehiculo tipoVehiculo = tipoVehiculoRepository
            .findById(dto.getIdTipoVehiculo())
            .orElseThrow(() -> new NotFoundException("Tipo de vehiculo no encontrado"));
        vehiculo.setIdTipoVehiculo(tipoVehiculo);
        vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public void eliminar(Long id) {
        Vehiculo vehiculo = vehiculoRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Vehiculo no encontrado"));
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
    }

    public List<Vehiculo> listarPorCliente(Long idClienteRef) {
        List<Vehiculo> vehiculos = vehiculoRepository.findByIdClienteRef(idClienteRef);
        if (vehiculos.isEmpty()) {
            throw new NotFoundException("No se encontraron vehiculos para el cliente");
        }
        return vehiculos;
    }

    public List<Vehiculo> listarPorTipoVehiculo(Long idTipoVehiculo) {
        List<Vehiculo> vehiculos = vehiculoRepository.findByIdTipoVehiculoId(idTipoVehiculo);
        if (vehiculos.isEmpty()) {
            throw new NotFoundException("No se encontraron vehiculos para el tipo de vehiculo");
        }
        return vehiculos;
    }

    public List<TipoVehiculo> listarTiposVehiculo() {
        List<TipoVehiculo> tiposVehiculo = tipoVehiculoRepository.findAll();
        if (tiposVehiculo.isEmpty()) {
            throw new NotFoundException("No se encontraron tipos de vehiculo");
        }
        return tiposVehiculo;
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
