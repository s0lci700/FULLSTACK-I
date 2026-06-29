package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccesoResponseDTO {

    @Schema(description = "ID del acceso", example = "1")
    private Long id;

    @Schema(description = "ID del vehículo", example = "1")
    private Long idVehiculo;

    @Schema(description = "ID del espacio", example = "1")
    private Long idEspacio;

    @Schema(description = "ID de la reserva asociada (opcional)", example = "1")
    private Long idReserva;

    @Schema(description = "Patente escaneada al ingresar", example = "ABC123")
    private String patenteEscaneada;

    @Schema(description = "Fecha y hora de entrada", example = "2026-07-01T10:00:00")
    private LocalDateTime fechaHoraEntrada;

    @Schema(description = "Fecha y hora de salida", example = "2026-07-01T12:00:00")
    private LocalDateTime fechaHoraSalida;

    @Schema(description = "Estado del acceso", example = "COMPLETADO")
    private String estado;

    @Schema(description = "Minutos de permanencia", example = "120")
    private Integer minutos;
}
