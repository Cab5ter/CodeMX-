package mx.codemx.modules.evaluacion;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;

public interface EvaluacionStrategy {

    ResultadoEvaluacion ejecutar(SolicitudEvaluacion solicitud, List<CasoPrueba> casos);
}
