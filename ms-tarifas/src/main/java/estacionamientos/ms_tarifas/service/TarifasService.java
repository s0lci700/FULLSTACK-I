package estacionamientos.ms_tarifas.service;

import estacionamientos.ms_tarifas.dto.TarifaCreateDTO;
import estacionamientos.ms_tarifas.dto.TarifaResponseDTO;
import estacionamientos.ms_tarifas.dto.TarifaUpdateDTO;
import estacionamientos.ms_tarifas.exception.ResourceNotFoundException;
import estacionamientos.ms_tarifas.model.Tarifas;
import estacionamientos.ms_tarifas.repository.TarifasRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarifasService {

    private static final Logger log = LoggerFactory.getLogger(TarifasService.class);

    private final TarifasRepository tarifasRepository;

    public TarifasService(TarifasRepository tarifasRepository) {
        this.tarifasRepository = tarifasRepository;
    }

    // Retorna todas las tarifas registradas
    public List<TarifaResponseDTO> findAll() {
        log.info("Obteniendo todas las tarifas");
        return tarifasRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // Busca tarifa por id, lanza 404 si no existe
    public TarifaResponseDTO findById(Long id) {
        log.info("Buscando tarifa con id: {}", id);
        Tarifas tarifa = tarifasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada con id: " + id));
        return toDTO(tarifa);
    }

    // Retorna la tarifa activa actualmente — la usa ms-pagos via Feign para calcular cobros
    public TarifaResponseDTO findVigente() {
        log.info("Obteniendo tarifa vigente");
        Tarifas tarifa = tarifasRepository.findByActivoTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No hay tarifa activa actualmente"));
        return toDTO(tarifa);
    }

    // Crea una nueva tarifa, valida que el nombre no este duplicado
    public TarifaResponseDTO create(TarifaCreateDTO dto) {
        log.info("Creando tarifa: {}", dto.getNombre());
        if (tarifasRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una tarifa con el nombre: " + dto.getNombre());
        }
        Tarifas tarifa = new Tarifas();
        tarifa.setNombre(dto.getNombre());
        tarifa.setDescripcion(dto.getDescripcion());
        tarifa.setPrecioBaseHora(dto.getPrecioBaseHora());
        tarifa.setActivo(dto.getActivo());
        Tarifas guardada = tarifasRepository.save(tarifa);
        log.info("Tarifa creada con id: {}", guardada.getId());
        return toDTO(guardada);
    }

    // Actualiza los datos de una tarifa existente
    public TarifaResponseDTO update(Long id, TarifaUpdateDTO dto) {
        log.info("Actualizando tarifa con id: {}", id);
        Tarifas tarifa = tarifasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada con id: " + id));
        if (!tarifa.getNombre().equals(dto.getNombre())
                && tarifasRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una tarifa con el nombre: " + dto.getNombre());
        }
        tarifa.setNombre(dto.getNombre());
        tarifa.setDescripcion(dto.getDescripcion());
        tarifa.setPrecioBaseHora(dto.getPrecioBaseHora());
        tarifa.setActivo(dto.getActivo());
        Tarifas actualizada = tarifasRepository.save(tarifa);
        log.info("Tarifa actualizada con id: {}", actualizada.getId());
        return toDTO(actualizada);
    }

    // Eliminacion logica — marca activo=false
    public void delete(Long id) {
        log.info("Desactivando tarifa con id: {}", id);
        Tarifas tarifa = tarifasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada con id: " + id));
        tarifa.setActivo(false);
        tarifasRepository.save(tarifa);
        log.info("Tarifa desactivada con id: {}", id);
    }

    // Convierte entidad a DTO — usado tambien por HorarioTarifasService
    public TarifaResponseDTO toDTO(Tarifas tarifa) {
        return new TarifaResponseDTO(
                tarifa.getId(),
                tarifa.getNombre(),
                tarifa.getDescripcion(),
                tarifa.getPrecioBaseHora(),
                tarifa.getActivo()
        );
    }

    // Retorna entidad directamente — usado internamente por HorarioTarifasService
    public Tarifas findEntityById(Long id) {
        return tarifasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada con id: " + id));
    }
}