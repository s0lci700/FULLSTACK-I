package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoResponseDTO {

    @Schema(description = "ID del método de pago", example = "1")
    private Long id;
    @Schema(description = "Nombre del método de pago", example = "Débito Banco Chile")
    private String nombre;
    @Schema(description = "Nombre del banco asociado", example = "Banco Chile")
    private String banco;
    @Schema(description = "Nombre del tipo de tarjeta", example = "Débito")
    private String tipoTarjeta;
}
