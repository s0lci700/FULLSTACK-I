package estacionamientos.ms_vehiculos.dto;

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

    @NotBlank
    @Size(min = 6, max = 6 )
    public String patente;

    @NotBlank
    public String marca;

    @NotBlank
    public String modelo;

    public String color;

    public Integer anio;

    @NotNull
    public Long idTipoVehiculo;

    @NotNull
    public Long idClienteRef;
}
