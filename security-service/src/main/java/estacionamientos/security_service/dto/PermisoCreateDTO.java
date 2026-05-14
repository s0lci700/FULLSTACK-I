package estacionamientos.security_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoCreateDTO {

    // Nombre del permiso, ej: CREATE_RESERVA, DELETE_ESPACIO
    // No puede estar vacio y no puede repetirse
    @NotBlank
    private String nombre;

    // Descripcion legible del permiso para saber que hace
    @NotBlank
    private String descripcion;
}