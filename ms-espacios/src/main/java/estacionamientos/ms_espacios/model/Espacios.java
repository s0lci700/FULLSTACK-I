package estacionamientos.ms_espacios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data @Entity

public class Espacios {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;

@Column(nullable = false, unique = true)
private String numero;

@Column(nullable = true)
private String zona;

@Column(nullable = false)
private Integer piso;

@Column(nullable = false)
private Integer disponible;

@Column(nullable = false)
private Integer activo;

}
