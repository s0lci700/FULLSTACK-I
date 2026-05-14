package estacionamientos.ms_accesos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsAccesosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAccesosApplication.class, args);
	}

}
