package mx.codemx.modules.evaluacion;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.RetosApi;
import org.springframework.stereotype.Service;

@Service
public class EvaluacionService implements EvaluacionApi {

    private final EvaluadorStrategyFactory factory;
    private final RetosApi retos;

    public EvaluacionService(EvaluadorStrategyFactory factory, RetosApi retos) {
        this.factory = factory;
        this.retos = retos;
    }

    @Override
    public ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud) {
        List<CasoPrueba> casos = retos.obtenerCasos(solicitud.retoId());

        try {
            return factory.crear(TipoEvaluacion.REMOTA).ejecutar(solicitud, casos);
        } catch (Exception e) {
            return factory.crear(TipoEvaluacion.LOCAL).ejecutar(solicitud, casos);
        }
    }
}
