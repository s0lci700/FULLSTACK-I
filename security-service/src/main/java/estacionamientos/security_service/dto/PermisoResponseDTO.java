package estacionamientos.security_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoResponseDTO {

    // ID generado por la base de datos
    private Long id;

    // Nombre tecnico del permiso
    private String nombre;

    // Descripcion legible del permiso
    private String descripcion;
}