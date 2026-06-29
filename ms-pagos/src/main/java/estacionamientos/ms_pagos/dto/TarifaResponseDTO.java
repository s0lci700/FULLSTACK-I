package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaResponseDTO {

    @Schema(description = "ID de la tarifa", example = "1")
    private Long id;
    @Schema(description = "Nombre de la tarifa", example = "Tarifa General")
    private String nombre;
    @Schema(description = "Descripción de la tarifa", example = "Tarifa estándar para todos los vehículos")
    private String descripcion;
    @Schema(description = "Precio base por hora", example = "1500.0")
    private Float precioBaseHora;
    @Schema(description = "Indica si la tarifa está activa", example = "true")
    private Boolean activo;
}
