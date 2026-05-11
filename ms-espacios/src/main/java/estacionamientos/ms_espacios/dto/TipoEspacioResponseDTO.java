package estacionamientos.ms_espacios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TipoEspacioResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double factorPrecio;
}