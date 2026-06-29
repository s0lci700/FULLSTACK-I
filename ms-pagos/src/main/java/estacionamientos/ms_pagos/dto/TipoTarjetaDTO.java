package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoTarjetaDTO {

    @Schema(description = "Nombre del tipo de tarjeta", example = "Débito")
    @NotBlank
    private String nombre;
}
