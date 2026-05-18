package estacionamientos.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteSuscripcionResponseDTO {
    private Long id;
    private ClienteResponseDTO cliente;
    private SuscripcionResponseDTO suscripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
}