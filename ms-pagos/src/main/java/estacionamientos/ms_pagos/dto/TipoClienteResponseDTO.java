package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoClienteResponseDTO {

    @Schema(description = "ID del tipo de cliente", example = "1")
    private Long id;
    @Schema(description = "Nombre del tipo de cliente", example = "Corporativo")
    private String nombre;
    @Schema(description = "Porcentaje de descuento aplicado a este tipo de cliente", example = "5.0")
    private java.math.BigDecimal descuentoPct;
}
