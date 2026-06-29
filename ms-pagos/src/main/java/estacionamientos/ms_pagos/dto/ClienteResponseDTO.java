package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    @Schema(description = "ID del cliente", example = "1")
    private Long id;
    @Schema(description = "Nombre del cliente", example = "María")
    private String nombre;
    @Schema(description = "Apellido del cliente", example = "González")
    private String apellido;
    @Schema(description = "Email del cliente", example = "maria@test.cl")
    private String email;
    @Schema(description = "Teléfono de contacto", example = "912345678")
    private String telefono;
    @Schema(description = "Tipo de cliente asociado")
    private TipoClienteResponseDTO tipoCliente;
    @Schema(description = "Indica si el cliente está activo", example = "true")
    private Boolean activo;
    @Schema(description = "Suscripción activa del cliente")
    private SuscripcionResponseDTO suscripcion;
}
