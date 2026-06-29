package estacionamientos.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @Schema(description = "Correo electrónico del usuario", example = "user@example.com", required = true)
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Contraseña del usuario", example = "secret123", minLength = 6, maxLength = 100)
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @Schema(description = "Nombre del rol asignado al usuario", example = "CLIENTE")
    private String nombreRol; // "CLIENTE" o "ADMIN"
}
