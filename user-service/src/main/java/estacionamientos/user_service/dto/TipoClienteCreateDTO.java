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
public class TipoClienteCreateDTO {

    @NotBlank
    private String nombre;

    @NotNull
    private BigDecimal descuentoPct;
}
