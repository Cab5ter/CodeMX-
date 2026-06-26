package mx.codemx.evaluacion.client;

import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EvaluadorClient {

    private final RestTemplate restTemplate;

    @Value("${evaluador.url}")
    private String evaluadorUrl;

    public EvaluadorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud) {
        return restTemplate.postForObject(
                evaluadorUrl + "/evaluar",
                solicitud,
                ResultadoEvaluacion.class
        );
    }
}
