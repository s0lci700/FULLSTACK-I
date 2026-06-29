package estacionamientos.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteCreateDTO {

    @Schema(description = "RUT del cliente", example = "11.111.111-1")
    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @Schema(description = "Nombre del cliente", example = "María")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "González")
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Schema(description = "Email del cliente", example = "maria@test.cl")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es valido")
    private String email;

    @Schema(description = "Teléfono de contacto", example = "912345678")
    private String telefono;

    @Schema(description = "ID del tipo de cliente", example = "1")
    @NotNull(message = "El tipo de cliente es obligatorio")
    private Long idTipoCliente;
}
