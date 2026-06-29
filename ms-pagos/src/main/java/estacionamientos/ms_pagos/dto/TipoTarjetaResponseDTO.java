package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoTarjetaResponseDTO {

    @Schema(description = "ID del tipo de tarjeta", example = "1")
    private Long id;
    @Schema(description = "Nombre del tipo de tarjeta", example = "Débito")
    private String nombre;
}
