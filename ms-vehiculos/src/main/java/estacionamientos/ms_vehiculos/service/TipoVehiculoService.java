package estacionamientos.ms_vehiculos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import estacionamientos.ms_vehiculos.dto.TipoVehiculoCreateDTO;
import estacionamientos.ms_vehiculos.dto.TipoVehiculoUpdateDTO;
import estacionamientos.ms_vehiculos.exception.ConflictException;
import estacionamientos.ms_vehiculos.exception.NotFoundException;
import estacionamientos.ms_vehiculos.model.TipoVehiculo;
import estacionamientos.ms_vehiculos.repository.TipoVehiculoRepository;
import estacionamientos.ms_vehiculos.repository.VehiculoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TipoVehiculoService {

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    public List<TipoVehiculo> listarTodos() {
        return tipoVehiculoRepository.findAll();
    }

    public TipoVehiculo obtenerPorId(Long id) {
        return tipoVehiculoRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo de vehiculo no encontrado"));
    }

    @Transactional
    public TipoVehiculo crear(TipoVehiculoCreateDTO tipoVehiculo) {
        if (tipoVehiculoRepository.findByNombre(tipoVehiculo.getNombre()) != null) {
            throw new ConflictException("El tipo de vehiculo ya existe");
        }
        TipoVehiculo nuevoTipo = new TipoVehiculo();
        nuevoTipo.setNombre(tipoVehiculo.getNombre());
        nuevoTipo.setDescripcion(tipoVehiculo.getDescripcion());
        nuevoTipo.setFactorPrecio(tipoVehiculo.getFactorPrecio());
        return tipoVehiculoRepository.save(nuevoTipo);
    }

    @Transactional
    public TipoVehiculo actualizar(Long id, TipoVehiculoUpdateDTO tipoVehiculo) {
        TipoVehiculo tipoExistente = tipoVehiculoRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo de vehiculo no encontrado"));
        tipoExistente.setNombre(tipoVehiculo.getNombre());
        tipoExistente.setDescripcion(tipoVehiculo.getDescripcion());
        tipoExistente.setFactorPrecio(tipoVehiculo.getFactorPrecio());
        return tipoVehiculoRepository.save(tipoExistente);
    }

    @Transactional
    public void eliminar(Long id) {
        tipoVehiculoRepository.findById(id).orElseThrow(() -> new NotFoundException("Tipo de vehiculo no encontrado"));
        if (!vehiculoRepository.findByIdTipoVehiculoId(id).isEmpty()) {
            throw new ConflictException("No se puede eliminar el tipo de vehiculo porque hay vehiculos asociados");
        }
        tipoVehiculoRepository.deleteById(id);
    }
}
