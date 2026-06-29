package estacionamientos.ms_reservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class VehiculoResponseDTO {

    @Schema(description = "ID del vehículo", example = "1")
    private Long id;
    @Schema(description = "ID del cliente propietario", example = "1")
    private Long idClienteRef;
    @Schema(description = "Indica si el vehículo está activo", example = "true")
    private Boolean activo;
}
