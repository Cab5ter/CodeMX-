package mx.codemx.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web: CORS para el frontend React (Vite), el cliente HTTP que usa la
 * estrategia de evaluación remota y los metadatos de Swagger/OpenAPI (ADR-04).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    OpenAPI codeMxOpenApi() {
        return new OpenAPI().info(new Info()
                .title("CodeMX API")
                .version("v1")
                .description("API REST del monolito modular de CodeMX (Spring Boot). "
                        + "Contrato documentado con Swagger/OpenAPI — ADR-04."));
    }
}
