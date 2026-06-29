package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CobroCreateDTO {

    @Schema(description = "ID del acceso a cobrar", example = "1")
    @NotNull
    private Long idAcceso;

    @Schema(description = "ID del método de pago", example = "1")
    @NotNull
    private Long idMetodoPago;

    @Schema(description = "ID del cliente que realiza el pago", example = "1")
    @NotNull
    private Long idCliente;
}
