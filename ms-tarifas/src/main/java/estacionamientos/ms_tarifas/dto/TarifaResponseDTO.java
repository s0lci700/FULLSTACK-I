package estacionamientos.ms_tarifas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TarifaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precioBaseHora;
    private Boolean activo;
}