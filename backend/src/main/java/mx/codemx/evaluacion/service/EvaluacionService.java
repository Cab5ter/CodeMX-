package mx.codemx.evaluacion.service;

import mx.codemx.evaluacion.api.EvaluacionApi;
import mx.codemx.evaluacion.client.EvaluadorClient;
import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;
import mx.codemx.retos.api.RetosApi;
import mx.codemx.retos.model.CasoPrueba;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluacionService implements EvaluacionApi {

    private final EvaluadorClient evaluadorClient;
    private final RetosApi retosApi;   // los casos de prueba son del módulo Retos: se piden por interfaz

    public EvaluacionService(EvaluadorClient evaluadorClient, RetosApi retosApi) {
        this.evaluadorClient = evaluadorClient;
        this.retosApi = retosApi;
    }

    @Override
    public ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud) {
        List<CasoPrueba> casos = retosApi.obtenerCasos(solicitud.retoId());
        return evaluadorClient.evaluar(solicitud, casos);
    }
}
