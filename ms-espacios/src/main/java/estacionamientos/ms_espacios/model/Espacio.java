package estacionamientos.ms_espacios.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data @Entity
@Table(name = "espacio")
public class Espacio {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long  id;

@Column(nullable = false, unique = true)
private String numero;

@Column(nullable = true)
private String zona;

@Column(nullable = false)
private Integer piso;

@ManyToOne
@JoinColumn(name= "id_tipo_espacio", nullable=false)
private TipoEspacio tipoEspacio;

@Column(nullable = false)
private Boolean disponible;

@Column(nullable = false)
private Boolean activo;

}
