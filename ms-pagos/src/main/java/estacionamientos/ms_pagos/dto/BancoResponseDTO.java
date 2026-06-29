package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BancoResponseDTO {

    @Schema(description = "ID del banco", example = "1")
    private Long id;
    @Schema(description = "Nombre del banco", example = "Banco Chile")
    private String nombre;
    @Schema(description = "Porcentaje de descuento del banco", example = "5.0")
    private BigDecimal descuento;
}
