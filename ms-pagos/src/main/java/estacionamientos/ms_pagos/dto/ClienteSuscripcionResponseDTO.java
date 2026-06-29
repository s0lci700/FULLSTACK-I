package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteSuscripcionResponseDTO {
    @Schema(description = "ID de la suscripción del cliente", example = "1")
    private Long id;
    @Schema(description = "Cliente asociado")
    private ClienteResponseDTO cliente;
    @Schema(description = "Suscripción asociada")
    private SuscripcionResponseDTO suscripcion;
    @Schema(description = "Fecha de inicio de la suscripción", example = "2026-07-01")
    private LocalDate fechaInicio;
    @Schema(description = "Fecha de fin de la suscripción", example = "2026-07-31")
    private LocalDate fechaFin;
    @Schema(description = "Indica si la suscripción está activa", example = "true")
    private Boolean activo;
}
