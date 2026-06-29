package estacionamientos.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @Schema(description = "Correo electrónico del usuario", example = "user@example.com", minLength = 3, maxLength = 50, required = true)
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Size(min = 3, max = 50, message = "El correo electrónico debe tener entre 3 y 50 caracteres")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "secret123", minLength = 6, maxLength = 100, required = true)
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;
}
