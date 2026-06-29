package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BancoDTO {

    @Schema(description = "Nombre del banco", example = "Banco Chile")
    @NotBlank
    private String nombre;

    @Schema(description = "Porcentaje de descuento del banco", example = "5.0")
    @NotNull
    private BigDecimal descuento;
}
