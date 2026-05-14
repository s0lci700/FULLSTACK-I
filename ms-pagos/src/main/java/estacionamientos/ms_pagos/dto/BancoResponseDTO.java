package estacionamientos.ms_pagos.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BancoResponseDTO {

    private Long id;
    private String nombre;
    private BigDecimal descuento;
}