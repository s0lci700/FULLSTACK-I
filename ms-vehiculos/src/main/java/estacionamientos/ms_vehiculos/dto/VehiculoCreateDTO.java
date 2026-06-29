package estacionamientos.ms_vehiculos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoCreateDTO {

    @Schema(description = "Patente del vehículo (6 caracteres)", example = "ABC123")
    @NotBlank
    @Size(min = 6, max = 6)
    public String patente;

    @Schema(description = "Marca del vehículo", example = "Toyota")
    @NotBlank
    public String marca;

    @Schema(description = "Modelo del vehículo", example = "Corolla")
    @NotBlank
    public String modelo;

    @Schema(description = "Color del vehículo", example = "Blanco")
    public String color;

    @Schema(description = "Año del vehículo", example = "2022")
    public Integer anio;

    @Schema(description = "ID del tipo de vehículo", example = "1")
    @NotNull
    public Long idTipoVehiculo;

    @Schema(description = "ID del cliente propietario", example = "1")
    @NotNull
    public Long idClienteRef;
}
