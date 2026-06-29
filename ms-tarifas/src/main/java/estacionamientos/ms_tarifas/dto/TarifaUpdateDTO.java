package estacionamientos.ms_tarifas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para actualizar una tarifa")
public class TarifaUpdateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio base por hora es obligatorio")
    private Double precioBaseHora;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}
