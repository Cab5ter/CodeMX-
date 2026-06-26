package mx.codemx.evaluador.service;

import mx.codemx.evaluador.client.RunnerClient;
import mx.codemx.evaluador.model.ResultadoEvaluacion;
import mx.codemx.evaluador.model.SolicitudEvaluacion;
import mx.codemx.retos.model.CasoPrueba;
import mx.codemx.retos.repository.CasoPruebaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Módulo Evaluador. Orquesta la evaluación de un envío: obtiene los casos de prueba
 * del reto y delega la ejecución del código al servicio Python a través del RunnerClient.
 */
@Service
public class EvaluadorService {

    private final RunnerClient runnerClient;
    private final CasoPruebaRepository casoPruebaRepository;

    public EvaluadorService(RunnerClient runnerClient, CasoPruebaRepository casoPruebaRepository) {
        this.runnerClient = runnerClient;
        this.casoPruebaRepository = casoPruebaRepository;
    }

    public ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud) {
        List<CasoPrueba> casos = casoPruebaRepository.findByRetoId(solicitud.retoId());
        return runnerClient.ejecutar(solicitud, casos);
    }
}
