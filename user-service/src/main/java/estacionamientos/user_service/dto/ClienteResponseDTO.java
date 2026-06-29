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
    @Schema(description = "Email del cliente", example = "maria@test.cl")
    private String email;
    @Schema(description = "Teléfono de contacto", example = "912345678")
    private String telefono;
    @Schema(description = "Tipo de cliente asociado")
    private TipoClienteResponseDTO tipoCliente;
    @Schema(description = "Indica si el cliente está activo", example = "true")
    private Boolean activo;
}