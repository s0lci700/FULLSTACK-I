package estacionamientos.ms_vehiculos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO: El componente scan arranca desde este paquete (estacionamientos.ms_vehiculos)
// pero las entidades, servicios y controladores están en estacionamientos.model/service/etc.
// Spring NO los va a encontrar. Solución: agregar scanBasePackages apuntando al paquete raíz.
// Ejemplo: @SpringBootApplication(scanBasePackages = "estacionamientos")
@SpringBootApplication(scanBasePackages = "estacionamientos")
public class MsVehiculosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsVehiculosApplication.class, args);
	}

}
