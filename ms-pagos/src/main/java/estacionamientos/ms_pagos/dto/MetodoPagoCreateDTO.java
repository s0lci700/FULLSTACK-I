package estacionamientos.ms_pagos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoCreateDTO {

    @NotBlank
    private String nombre;

    private Long idBanco;

    private Long idTipoTarjeta;
}