package estacionamientos.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoEspacioResponseDTO {
    private Long id;
    private Double factorPrecio;
}
