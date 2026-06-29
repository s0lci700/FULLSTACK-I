package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoEspacioResponseDTO {
    @Schema(description = "ID del tipo de espacio", example = "1")
    private Long id;
    @Schema(description = "Factor de precio aplicado al tipo de espacio", example = "1.2")
    private Float factorPrecio;
}
