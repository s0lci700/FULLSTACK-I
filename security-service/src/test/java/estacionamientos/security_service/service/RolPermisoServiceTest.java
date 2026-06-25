package estacionamientos.security_service.service;

import estacionamientos.security_service.dto.RolPermisoCreateDTO;
import estacionamientos.security_service.dto.RolPermisoResponseDTO;
import estacionamientos.security_service.exception.BusinessException;
import estacionamientos.security_service.exception.ResourceNotFoundException;
import estacionamientos.security_service.model.Permiso;
import estacionamientos.security_service.model.RolPermiso;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolPermisoServiceTest {

    @Mock private RolPermisoRepository rolPermisoRepository;
    @Mock private PermisoRepository permisoRepository;

    @InjectMocks
    private RolPermisoService rolPermisoService;

    private Permiso permiso;
    private RolPermiso rolPermiso;

    @BeforeEach
    void setUp() {
        permiso = new Permiso(1L, "LEER_VEHICULOS", "vehiculos", "GET");
        rolPermiso = new RolPermiso(1L, 2L, permiso);
    }

    @Test
    @DisplayName("findAll retorna todas las asignaciones rol-permiso como DTOs")
    void findAll_debeRetornarListaDeDTOs() {
        when(rolPermisoRepository.findAll()).thenReturn(List.of(rolPermiso));

        List<RolPermisoResponseDTO> resultado = rolPermisoService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdRol()).isEqualTo(2L);
    }

    @Test
    @DisplayName("findByIdRol retorna los permisos asignados al rol indicado")
    void findByIdRol_debeRetornarPermisosDelRol() {
        when(rolPermisoRepository.findAllByIdRol(2L)).thenReturn(List.of(rolPermiso));

        List<RolPermisoResponseDTO> resultado = rolPermisoService.findByIdRol(2L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPermiso().getNombre()).isEqualTo("LEER_VEHICULOS");
    }

    @Test
    @DisplayName("create lanza ResourceNotFoundException cuando el permiso no existe")
    void create_permisoNoExiste_debeLanzarNotFound() {
        when(permisoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rolPermisoService.create(new RolPermisoCreateDTO(1L, 99L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create lanza BusinessException cuando el permiso ya está asignado al rol")
    void create_asignacionDuplicada_debeLanzarBusinessException() {
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permiso));
        when(rolPermisoRepository.existsByIdRolAndPermisoId(2L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> rolPermisoService.create(new RolPermisoCreateDTO(2L, 1L)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("create guarda y retorna la asignación cuando es nueva")
    void create_nuevo_debeGuardarYRetornarDTO() {
        when(permisoRepository.findById(1L)).thenReturn(Optional.of(permiso));
        when(rolPermisoRepository.existsByIdRolAndPermisoId(2L, 1L)).thenReturn(false);
        when(rolPermisoRepository.save(any(RolPermiso.class))).thenAnswer(inv -> {
            RolPermiso rp = inv.getArgument(0);
            rp.setId(1L);
            return rp;
        });

        RolPermisoResponseDTO resultado = rolPermisoService.create(new RolPermisoCreateDTO(2L, 1L));

        assertThat(resultado.getIdRol()).isEqualTo(2L);
        assertThat(resultado.getPermiso().getNombre()).isEqualTo("LEER_VEHICULOS");
    }

    @Test
    @DisplayName("delete lanza ResourceNotFoundException cuando la asignación no existe")
    void delete_noExiste_debeLanzarNotFound() {
        when(rolPermisoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> rolPermisoService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete elimina la asignación cuando existe")
    void delete_existente_debeEliminar() {
        when(rolPermisoRepository.existsById(1L)).thenReturn(true);

        rolPermisoService.delete(1L);

        verify(rolPermisoRepository).deleteById(1L);
    }
}
