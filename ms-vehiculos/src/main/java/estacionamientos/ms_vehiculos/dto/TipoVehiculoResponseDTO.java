package estacionamientos.ms_vehiculos.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoVehiculoResponseDTO {


    @NotBlank
    private String nombre;

    private String descripcion;

    @NotBlank
    @Positive
    private BigDecimal factorPrecio;
}
