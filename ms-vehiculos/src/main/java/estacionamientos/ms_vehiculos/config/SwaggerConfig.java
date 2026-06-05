package estacionamientos.ms_vehiculos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Estacionamiento - Vehículos")
                .version("1.0.0")
                .description("Registro y gestión de vehículos y tipos de vehículo del estacionamiento."));
    }
}
