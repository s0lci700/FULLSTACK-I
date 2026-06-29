package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuscripcionResponseDTO {
    @Schema(description = "ID de la suscripción", example = "1")
    private Long id;
    @Schema(description = "Nombre del plan de suscripción", example = "Plan Mensual")
    private String nombre;
    @Schema(description = "Descripción del plan", example = "Acceso ilimitado durante 30 días")
    private String descripcion;
    @Schema(description = "Porcentaje de descuento aplicado", example = "10.5")
    private BigDecimal DescuentoPct;
    @Schema(description = "Indica si el plan está activo", example = "true")
    private Boolean activo;
}
