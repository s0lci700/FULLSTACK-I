package estacionamientos.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private TipoClienteResponseDTO tipoCliente;
    private Boolean activo;
}