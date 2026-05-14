package estacionamientos.ms_pagos.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccesoResponseDTO {

    private Long id;

    private Long idVehiculo;

    private Long idEspacio;

    private Long idReserva;

    private String patenteEscaneada;

    private LocalDateTime fechaHoraEntrada;

    private LocalDateTime fechaHoraSalida;

    private String estado;
    
    private Integer minutos; // confirmar con Sol
}
