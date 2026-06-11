package estacionamientos.ms_vehiculos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import estacionamientos.ms_vehiculos.dto.TipoVehiculoResponseDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoCreateDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoResponseDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoUpdateDTO;
import estacionamientos.ms_vehiculos.exception.ConflictException;
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

    public List<VehiculoResponseDTO> listarTodos() {
        return vehiculoRepository.findAll().stream().map(this::toVehiculoDTO).toList();
    }

    public VehiculoResponseDTO obtenerPorId(Long id) {
        Vehiculo vehiculo = vehiculoRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Vehiculo no encontrado"));
        return toVehiculoDTO(vehiculo);
    }

    @Transactional
    public VehiculoResponseDTO crear(VehiculoCreateDTO dto) {
        if (vehiculoRepository.findByPatente(dto.patente).isPresent()) {
            throw new ConflictException("Ya se encuentra la patente");
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
        return toVehiculoDTO(vehiculoRepository.save(vehiculo));
    }

    @Transactional
    public VehiculoResponseDTO actualizar(Long id, VehiculoUpdateDTO dto) {
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
        return toVehiculoDTO(vehiculo);
    }

    @Transactional
    public void eliminar(Long id) {
        Vehiculo vehiculo = vehiculoRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Vehiculo no encontrado"));
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
    }

    public List<VehiculoResponseDTO> listarPorCliente(Long idClienteRef) {
        List<Vehiculo> vehiculos = vehiculoRepository.findByIdClienteRef(idClienteRef);
        return vehiculos.stream().map(this::toVehiculoDTO).toList();
    }

    public List<VehiculoResponseDTO> listarPorTipoVehiculo(Long idTipoVehiculo) {
        List<Vehiculo> vehiculos = vehiculoRepository.findByIdTipoVehiculoId(idTipoVehiculo);
        return vehiculos.stream().map(this::toVehiculoDTO).toList();
    }

    public List<TipoVehiculoResponseDTO> listarTiposVehiculo() {
        List<TipoVehiculo> tiposVehiculo = tipoVehiculoRepository.findAll();
        return tiposVehiculo.stream().map(this::toTipoVehiculoDTO).toList();
    }

    public VehiculoResponseDTO toVehiculoDTO(Vehiculo vehiculo) {
        return new VehiculoResponseDTO(
            vehiculo.getId(),
            vehiculo.getMarca(),
            vehiculo.getModelo(),
            vehiculo.getColor(),
            vehiculo.getPatente(),
            vehiculo.getAnio(),
            vehiculo.getIdTipoVehiculo().getId(),
            vehiculo.getIdClienteRef(),
            vehiculo.getActivo()
        );
    }

    public TipoVehiculoResponseDTO toTipoVehiculoDTO(TipoVehiculo tipoVehiculo) {
        return new TipoVehiculoResponseDTO(
            tipoVehiculo.getNombre(),
            tipoVehiculo.getDescripcion(),
            tipoVehiculo.getFactorPrecio()
        );
    }
}
