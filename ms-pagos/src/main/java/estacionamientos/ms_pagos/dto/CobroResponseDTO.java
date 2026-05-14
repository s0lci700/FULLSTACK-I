package estacionamientos.ms_pagos.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CobroResponseDTO {

    private Long id;
    private Long idAcceso;
    private Long idCliente;
    private Long minutos;
    private Double montoBase;
    private Double montoFinal;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCobro;
    private String metodoPago;
}