package estacionamientos.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoResponseDTO {

    private Long id;
    private String nombre;
    private String banco;
    private String tipoTarjeta;
}