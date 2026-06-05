package estacionamientos.ms_reservas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Estacionamiento - Reservas")
                .version("1.0.0")
                .description("Gestión del ciclo de vida de reservas: creación, confirmación, cancelación y finalización."));
    }
}
