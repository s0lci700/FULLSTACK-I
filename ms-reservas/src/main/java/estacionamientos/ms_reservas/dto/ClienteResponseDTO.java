package estacionamientos.ms_reservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ClienteResponseDTO {

    @Schema(description = "ID del cliente", example = "1")
    private Long id;
    @Schema(description = "Indica si el cliente está activo", example = "true")
    private Boolean activo;
}
