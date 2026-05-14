package estacionamientos.ms_pagos.dto;

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

    @NotBlank
    private String nombre;

    @NotNull
    private BigDecimal descuento;
}