package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspacioResponseDTO {
    @Schema(description = "ID del espacio", example = "1")
    private Long id;
    @Schema(description = "Tipo de espacio asociado")
    private TipoEspacioResponseDTO tipoEspacio;
}
