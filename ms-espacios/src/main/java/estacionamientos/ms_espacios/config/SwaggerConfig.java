package estacionamientos.ms_espacios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Estacionamiento - Espacios")
                .version("1.0.0")
                .description("Gestión de espacios de estacionamiento y su disponibilidad en tiempo real."));
    }
}
