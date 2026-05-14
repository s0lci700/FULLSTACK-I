package estacionamientos.ms_pagos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CobroCreateDTO {

    @NotNull
    private Long idAcceso;

    @NotNull
    private Long idMetodoPago;
}