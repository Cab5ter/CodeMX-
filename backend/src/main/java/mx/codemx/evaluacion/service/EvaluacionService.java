package mx.codemx.evaluacion.service;

import mx.codemx.evaluacion.client.EvaluadorClient;
import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;
import mx.codemx.retos.model.CasoPrueba;
import mx.codemx.retos.repository.CasoPruebaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluacionService {

    private final EvaluadorClient evaluadorClient;
    private final CasoPruebaRepository casoPruebaRepository;

    public EvaluacionService(EvaluadorClient evaluadorClient, CasoPruebaRepository casoPruebaRepository) {
        this.evaluadorClient = evaluadorClient;
        this.casoPruebaRepository = casoPruebaRepository;
    }

    public ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud) {
        List<CasoPrueba> casos = casoPruebaRepository.findByRetoId(solicitud.retoId());
        return evaluadorClient.evaluar(solicitud, casos);
    }
}
