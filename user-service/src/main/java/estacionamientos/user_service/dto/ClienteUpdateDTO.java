package estacionamientos.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteUpdateDTO {

    @Schema(description = "Nombre del cliente", example = "María")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "González")
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Schema(description = "Teléfono de contacto", example = "912345678")
    private String telefono;

    @Schema(description = "ID del tipo de cliente", example = "1")
    @NotNull(message = "El tipo de cliente es obligatorio")
    private Long idTipoCliente;
}
