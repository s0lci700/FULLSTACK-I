package estacionamientos.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Float precioBaseHora;
    private Boolean activo;
}