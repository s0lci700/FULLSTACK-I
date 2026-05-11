package estacionamientos.ms_espacios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspacioCreateDTO {

    @NotBlank(message = "El numero es obligatorio")
    private String numero;

    private String zona;

    @NotNull(message = "El piso es obligatorio")
    private Integer piso;

    @NotNull(message = "El id del tipo de espacio es obligatorio")
    private Long idTipoEspacio;

    @NotNull(message = "La disponibilidad es obligatoria")
    private Boolean disponible;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}