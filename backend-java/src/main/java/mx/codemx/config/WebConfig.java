package mx.codemx.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebConfig {

    public static final String ESQUEMA_JWT = "bearer-jwt";

    private final List<String> origenesPermitidos;

    public WebConfig(@Value("${codemx.cors.origenes:http://localhost:[*],http://127.0.0.1:[*]}")
                     List<String> origenesPermitidos) {
        this.origenesPermitidos = origenesPermitidos;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOriginPatterns(origenesPermitidos);
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        return fuente;
    }

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    OpenAPI codeMxOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CodeMX API")
                        .version("v1")
                        .description("""
                                API REST del monolito modular de CodeMX (Spring Boot). Contrato \
                                documentado con Swagger/OpenAPI — ADR-04.

                                **Casi todos los endpoints exigen un JWT.** Solo son públicos \
                                `POST /auth/login`, el alta de cuenta en `POST /api/usuarios` y esta \
                                documentación. Para probar el resto: crea una cuenta o inicia sesión, \
                                copia el `token` de la respuesta, pulsa **Authorize** aquí arriba y \
                                pégalo. Swagger lo mandará como `Authorization: Bearer <token>`."""))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT))
                .components(new Components().addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
                        .name(ESQUEMA_JWT)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Pega aquí el token que devuelve POST /auth/login (sin la palabra Bearer).")));
    }
}
