package estacionamientos.ms_vehiculos.service;

import estacionamientos.ms_vehiculos.dto.VehiculoCreateDTO;
import estacionamientos.ms_vehiculos.dto.VehiculoResponseDTO;
import estacionamientos.ms_vehiculos.exception.ConflictException;
import estacionamientos.ms_vehiculos.exception.NotFoundException;
import estacionamientos.ms_vehiculos.model.TipoVehiculo;
import estacionamientos.ms_vehiculos.model.Vehiculo;
import estacionamientos.ms_vehiculos.repository.TipoVehiculoRepository;
import estacionamientos.ms_vehiculos.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private TipoVehiculoRepository tipoVehiculoRepository;

    @InjectMocks
    private VehiculoService vehiculoService;

    private TipoVehiculo tipoAuto;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        tipoAuto = new TipoVehiculo(1L, "Auto", "Vehículo liviano", new BigDecimal("1.00"));
        vehiculo = new Vehiculo(1L, "ABCD12", "Toyota", "Yaris", "Rojo", 2020, tipoAuto, 1L, true);
    }

    @Test
    @DisplayName("listarTodos debe retornar los vehículos como DTOs")
    void listarTodos_debeRetornarDTOs() {
        // Arrange
        when(vehiculoRepository.findAll()).thenReturn(List.of(vehiculo));

        // Act
        List<VehiculoResponseDTO> resultado = vehiculoService.listarTodos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPatente()).isEqualTo("ABCD12");
        assertThat(resultado.get(0).getIdTipoVehiculo()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerPorId debe lanzar NotFoundException cuando el vehículo no existe")
    void obtenerPorId_inexistente_debeLanzarNotFound() {
        // Arrange
        when(vehiculoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> vehiculoService.obtenerPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehiculo no encontrado");
    }

    @Test
    @DisplayName("crear debe lanzar ConflictException cuando la patente ya existe")
    void crear_patenteDuplicada_debeLanzarConflict() {
        // Arrange
        VehiculoCreateDTO dto = new VehiculoCreateDTO("ABCD12", "Toyota", "Yaris", "Rojo", 2020, 1L, 1L);
        when(vehiculoRepository.findByPatente("ABCD12")).thenReturn(Optional.of(vehiculo));

        // Act + Assert
        assertThatThrownBy(() -> vehiculoService.crear(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("patente");
        verify(vehiculoRepository, never()).save(any(Vehiculo.class));
    }

    @Test
    @DisplayName("crear debe guardar el vehículo cuando la patente es nueva y el tipo existe")
    void crear_valido_debeGuardarYRetornarDTO() {
        // Arrange
        VehiculoCreateDTO dto = new VehiculoCreateDTO("WXYZ98", "Honda", "Civic", "Azul", 2022, 1L, 2L);
        when(vehiculoRepository.findByPatente("WXYZ98")).thenReturn(Optional.empty());
        when(tipoVehiculoRepository.findById(1L)).thenReturn(Optional.of(tipoAuto));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenAnswer(inv -> {
            Vehiculo v = inv.getArgument(0);
            v.setId(5L);
            return v;
        });

        // Act
        VehiculoResponseDTO resultado = vehiculoService.crear(dto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getPatente()).isEqualTo("WXYZ98");
        assertThat(resultado.getIdClienteRef()).isEqualTo(2L);
    }

    @Test
    @DisplayName("crear debe lanzar NotFoundException cuando el tipo de vehículo no existe")
    void crear_tipoInexistente_debeLanzarNotFound() {
        // Arrange
        VehiculoCreateDTO dto = new VehiculoCreateDTO("WXYZ98", "Honda", "Civic", "Azul", 2022, 77L, 2L);
        when(vehiculoRepository.findByPatente("WXYZ98")).thenReturn(Optional.empty());
        when(tipoVehiculoRepository.findById(77L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> vehiculoService.crear(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Tipo de vehiculo no encontrado");
    }

    @Test
    @DisplayName("eliminar debe hacer borrado lógico dejando activo=false")
    void eliminar_debeDesactivarVehiculo() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        vehiculoService.eliminar(1L);

        // Assert
        ArgumentCaptor<Vehiculo> captor = ArgumentCaptor.forClass(Vehiculo.class);
        verify(vehiculoRepository).save(captor.capture());
        assertThat(captor.getValue().getActivo()).isFalse();
    }
}
