package example.ms_administrador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // <--- CRUCIAL
public class MsAdministradorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAdministradorApplication.class, args);
	}

}
