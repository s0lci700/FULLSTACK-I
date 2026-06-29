package estacionamientos.ms_tarifas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta para tarifa")
public class TarifaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precioBaseHora;
    private Boolean activo;
}
