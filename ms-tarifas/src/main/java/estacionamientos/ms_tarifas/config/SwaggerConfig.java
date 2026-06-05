package estacionamientos.ms_tarifas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Estacionamiento - Tarifas")
                .version("1.0.0")
                .description("Gestión de tarifas base y horarios de tarifa. Expone endpoints vigentes consumidos por ms-pagos."));
    }
}
