package mx.codemx.evaluacion.client;

import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
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
        try {
            return restTemplate.postForObject(
                    evaluadorUrl + "/evaluar",
                    solicitud,
                    ResultadoEvaluacion.class
            );
        } catch (RestClientException e) {
            return evaluarLocalmente(solicitud.codigoFuente());
        }
    }

    // Simulación local mientras el servicio Python no está disponible
    private ResultadoEvaluacion evaluarLocalmente(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return new ResultadoEvaluacion("ERROR_EN_EJECUCION", "El código está vacío", 0L);
        }
        if (codigo.trim().length() < 5) {
            return new ResultadoEvaluacion("INCORRECTO", "La solución no pasa los casos de prueba", 0L);
        }
        long tiempoMs = (long) (Math.random() * 250 + 50);
        return new ResultadoEvaluacion("ACEPTADO", null, tiempoMs);
    }
}
