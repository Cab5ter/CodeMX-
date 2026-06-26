package mx.codemx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * RestTemplate usado por el módulo Evaluador para llamar al servicio Python.
     * Se le aplica el timeout explícito (evaluador.timeout-ms) que exige el ADR-02:
     * la Vista de Procesos identificó que el evaluador debe tener un límite de tiempo
     * para no bloquear al backend si el runner de código se cuelga.
     */
    @Bean
    public RestTemplate restTemplate(@Value("${evaluador.timeout-ms}") int timeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        return new RestTemplate(factory);
    }
}
