package mx.codemx.evaluacion.service;

import mx.codemx.evaluacion.client.EvaluadorClient;
import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;
import org.springframework.stereotype.Service;

@Service
public class EvaluacionService {

    private final EvaluadorClient evaluadorClient;

    public EvaluacionService(EvaluadorClient evaluadorClient) {
        this.evaluadorClient = evaluadorClient;
    }

    public ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud) {
        return evaluadorClient.evaluar(solicitud);
    }
}
