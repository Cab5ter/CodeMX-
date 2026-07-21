package mx.codemx.modules.evaluacion;

import java.time.Duration;
import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Estrategia concreta (Strategy): envía el código al Servicio Python externo (sandbox)
 * por HTTP/JSON con un timeout explícito. Si el servicio no responde, lanza excepción
 * para que el contexto (EvaluacionService) recurra a la estrategia de respaldo.
 */
@Component
public class EvaluacionRemotaStrategy implements EvaluacionStrategy {

    private final RestClient http;

    public EvaluacionRemotaStrategy(
            RestClient.Builder builder,
            @Value("${evaluador.url:http://localhost:8000}") String url,
            @Value("${evaluador.timeout-ms:5000}") int timeoutMs) {

        Duration timeout = Duration.ofMillis(timeoutMs);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);

        this.http = builder.baseUrl(url).requestFactory(factory).build();
    }

    @Override
    public ResultadoEvaluacion ejecutar(SolicitudEvaluacion solicitud, List<CasoPrueba> casos) {
        ResultadoEvaluacion resultado = http.post()
                .uri("/evaluar")
                .body(solicitud)
                .retrieve()
                .body(ResultadoEvaluacion.class);

        if (resultado == null) {
            throw new IllegalStateException("Respuesta vacía del Servicio Python");
        }
        return resultado;
    }
}
