package estacionamientos.ms_reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponseDTO {
    @Schema(description = "ID del vehículo", example = "1")
    private Long id;
    @Schema(description = "Marca del vehículo", example = "Toyota")
    private String marca;
    @Schema(description = "Modelo del vehículo", example = "Corolla")
    private String modelo;
    @Schema(description = "Color del vehículo", example = "Blanco")
    private String color;
    @Schema(description = "Patente del vehículo", example = "ABC123")
    private String patente;
    @Schema(description = "Año del vehículo", example = "2022")
    private Integer anio;
    @Schema(description = "ID del tipo de vehículo", example = "1")
    private Long idTipoVehiculo;
    @Schema(description = "ID del cliente propietario", example = "1")
    private Long idClienteRef;
    @Schema(description = "Indica si el vehículo está activo", example = "true")
    private Boolean activo;
}
