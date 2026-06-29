package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioTarifaResponseDTO {
    @Schema(description = "ID del horario de tarifa", example = "1")
    private Long id;
    @Schema(description = "Multiplicador de precio para este horario", example = "1.5")
    private Float multiplicador;
}
