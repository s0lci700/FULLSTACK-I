package estacionamientos.ms_pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoUpdateDTO {

    @Schema(description = "Nombre del método de pago", example = "Débito Banco Chile")
    @NotBlank
    private String nombre;

    @Schema(description = "ID del cliente propietario", example = "1")
    private Long idClienteRef;

    @Schema(description = "ID del banco asociado", example = "1")
    @NotNull
    private Long idBanco;

    @Schema(description = "ID del tipo de tarjeta", example = "1")
    @NotNull
    private Long idTipoTarjeta;

    @Schema(description = "Últimos 4 dígitos de la tarjeta", example = "1234")
    @Pattern(regexp = "\\d{4}")
    private String ultimos4;

    @Schema(description = "Mes de vencimiento de la tarjeta", example = "12")
    @Min(1)
    @Max(12)
    private Integer mesVencimiento;

    @Schema(description = "Año de vencimiento de la tarjeta", example = "2028")
    @Min(2024)
    private Integer anioVencimiento;
}
