package estacionamientos.ms_reportes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Estacionamiento - Reportes")
                .version("1.0.0")
                .description("Reportes de ocupación, accesos y cobros. Servicio de solo lectura que consume datos vía Feign."));
    }
}
