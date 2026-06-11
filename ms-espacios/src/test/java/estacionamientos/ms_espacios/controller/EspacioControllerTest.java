package estacionamientos.ms_espacios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import estacionamientos.ms_espacios.dto.EspacioCreateDTO;
import estacionamientos.ms_espacios.dto.EspacioResponseDTO;
import estacionamientos.ms_espacios.dto.TipoEspacioResponseDTO;
import estacionamientos.ms_espacios.exception.ResourceNotFoundException;
import estacionamientos.ms_espacios.service.EspacioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EspacioController.class)
@AutoConfigureMockMvc(addFilters = false)
class EspacioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EspacioService espacioService;

    private EspacioResponseDTO espacioDTO() {
        TipoEspacioResponseDTO tipo = new TipoEspacioResponseDTO(1L, "Normal", "Espacio estándar", new BigDecimal("1.00"));
        return new EspacioResponseDTO(1L, "A-01", "Norte", 1, tipo, true, true);
    }

    @Test
    @DisplayName("GET /api/espacios debe retornar 200 con la lista de espacios")
    void getAll_debeRetornar200ConLista() throws Exception {
        // Arrange
        when(espacioService.findAll()).thenReturn(List.of(espacioDTO()));

        // Act + Assert
        mockMvc.perform(get("/api/espacios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value("A-01"))
                .andExpect(jsonPath("$[0].tipoEspacio.nombre").value("Normal"));
    }

    @Test
    @DisplayName("GET /api/espacios/{id} debe retornar 404 cuando el espacio no existe")
    void getById_inexistente_debeRetornar404() throws Exception {
        // Arrange
        when(espacioService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Espacio no encontrado con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/espacios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.mensaje").value("Espacio no encontrado con id: 99"));
    }

    @Test
    @DisplayName("POST /api/espacios debe retornar 201 cuando el espacio se crea")
    void create_valido_debeRetornar201() throws Exception {
        // Arrange
        EspacioCreateDTO dto = new EspacioCreateDTO("B-02", "Sur", 2, 1L, true, true);
        when(espacioService.create(any(EspacioCreateDTO.class))).thenReturn(espacioDTO());

        // Act + Assert
        mockMvc.perform(post("/api/espacios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("POST /api/espacios sin campos obligatorios debe retornar 400 con detalle de campos")
    void create_invalido_debeRetornar400() throws Exception {
        // Arrange: DTO sin numero, piso ni tipo (violan @NotBlank/@NotNull)
        EspacioCreateDTO dto = new EspacioCreateDTO(null, "Sur", null, null, null, null);

        // Act + Assert
        mockMvc.perform(post("/api/espacios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.numero").exists());
    }

    @Test
    @DisplayName("PUT /api/espacios/{id}/disponibilidad debe retornar 200 con mensaje")
    void updateDisponibilidad_debeRetornar200() throws Exception {
        // Act + Assert
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/espacios/1/disponibilidad")
                        .param("disponible", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Disponibilidad actualizada correctamente"));
    }
}
