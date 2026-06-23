package estacionamientos.security_service.service;

import estacionamientos.security_service.dto.PermisoCreateDTO;
import estacionamientos.security_service.dto.PermisoResponseDTO;
import estacionamientos.security_service.exception.BusinessException;
import estacionamientos.security_service.exception.ResourceNotFoundException;
import estacionamientos.security_service.model.Permiso;
import estacionamientos.security_service.repository.PermisoRepository;
import estacionamientos.security_service.repository.RolPermisoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermisoServiceTest {

    @Mock private PermisoRepository permisoRepository;
    @Mock private RolPermisoRepository rolPermisoRepository;

    @InjectMocks
    private PermisoService permisoService;

    private Permiso permiso;

    @BeforeEach
    void setUp() {
        permiso = new Permiso(1L, "LEER_VEHICULOS", "vehiculos", "GET");
    }

    @Test
    @DisplayName("findAll debe retornar la lista de permisos como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        // Arrange
        when(permisoRepository.findAll()).thenReturn(List.of(permiso));

        // Act
        List<PermisoResponseDTO> resultado = permisoService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("LEER_VEHICULOS");
    }

    @Test
    @DisplayName("findById debe retornar el permiso cuando existe")
    void findById_existente_debeRetornarDTO() {
        // Arrange
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permiso));

        // Act
        PermisoResponseDTO resultado = permisoService.findById(1L);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getRecurso()).isEqualTo("vehiculos");
    }

    @Test
    @DisplayName("findById debe lanzar ResourceNotFoundException cuando no existe")
    void findById_noExiste_debeLanzarNotFound() {
        // Arrange
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> permisoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create debe lanzar BusinessException cuando el nombre ya existe")
    void create_nombreDuplicado_debeLanzarBusinessException() {
        // Arrange
        when(permisoRepository.existsByNombre("LEER_VEHICULOS")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> permisoService.create(new PermisoCreateDTO("LEER_VEHICULOS", "vehiculos", "GET")))
                .isInstanceOf(BusinessException.class);
        verify(permisoRepository, never()).save(any());
    }

    @Test
    @DisplayName("create debe guardar el permiso cuando el nombre es nuevo")
    void create_nombreNuevo_debeGuardarYRetornarDTO() {
        // Arrange
        PermisoCreateDTO dto = new PermisoCreateDTO("ESCRIBIR_ESPACIOS", "espacios", "POST");
        when(permisoRepository.existsByNombre("ESCRIBIR_ESPACIOS")).thenReturn(false);
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(inv -> {
            Permiso p = inv.getArgument(0);
            p.setId(2L);
            return p;
        });

        // Act
        PermisoResponseDTO resultado = permisoService.create(dto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getNombre()).isEqualTo("ESCRIBIR_ESPACIOS");
    }

    @Test
    @DisplayName("update debe actualizar y retornar el permiso modificado")
    void update_existente_debeActualizarYRetornarDTO() {
        // Arrange
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permiso));
        when(permisoRepository.save(any(Permiso.class))).thenAnswer(inv -> inv.getArgument(0));
        PermisoCreateDTO dto = new PermisoCreateDTO("LEER_VEHICULOS_V2", "vehiculos", "GET");

        // Act
        PermisoResponseDTO resultado = permisoService.update(1L, dto);

        // Assert
        assertThat(resultado.getNombre()).isEqualTo("LEER_VEHICULOS_V2");
    }

    @Test
    @DisplayName("delete debe lanzar ResourceNotFoundException cuando el permiso no existe")
    void delete_noExiste_debeLanzarNotFound() {
        // Arrange
        when(permisoRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> permisoService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete debe eliminar el permiso cuando existe")
    void delete_existente_debeEliminarPermiso() {
        // Arrange
        when(permisoRepository.existsById(1L)).thenReturn(true);
        when(rolPermisoRepository.findAllByPermisoId(1L)).thenReturn(List.of());

        // Act
        permisoService.delete(1L);

        // Assert
        verify(permisoRepository).deleteById(1L);
    }
}
