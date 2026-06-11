package estacionamientos.ms_vehiculos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Respuesta con los datos de un vehículo registrado")
@Data @AllArgsConstructor @NoArgsConstructor
public class VehiculoResponseDTO {

    @Schema(description = "Identificador único del vehículo", example = "1")
    private Long id;

    @Schema(description = "Marca del vehículo", example = "Toyota")
    private String marca;

    @Schema(description = "Modelo del vehículo", example = "Yaris")
    private String modelo;

    @Schema(description = "Color del vehículo", example = "Rojo")
    private String color;

    @Schema(description = "Patente chilena (inmutable tras la creación)", example = "ABCD12")
    private String patente;

    @Schema(description = "Año de fabricación", example = "2020")
    private Integer anio;

    @Schema(description = "ID del tipo de vehículo", example = "1")
    private Long idTipoVehiculo;

    @Schema(description = "FK lógica al cliente dueño (db_usuarios)", example = "1")
    private Long idClienteRef;

    @Schema(description = "Borrado lógico: false = desactivado", example = "true")
    private Boolean activo;
}
