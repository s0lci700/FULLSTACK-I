package estacionamientos.user_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuscripcionCreateDTO {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    private BigDecimal precio;

    private BigDecimal descuentoPct;

    @NotNull
    private Integer duracionDias;
}
