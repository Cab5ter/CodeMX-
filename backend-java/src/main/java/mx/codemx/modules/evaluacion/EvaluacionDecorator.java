package mx.codemx.modules.evaluacion;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;

public abstract class EvaluacionDecorator implements EvaluacionStrategy {

    protected final EvaluacionStrategy envuelto;

    protected EvaluacionDecorator(EvaluacionStrategy envuelto) {
        this.envuelto = envuelto;
    }

    @Override
    public ResultadoEvaluacion ejecutar(SolicitudEvaluacion solicitud, List<CasoPrueba> casos) {
        return envuelto.ejecutar(solicitud, casos);
    }
}
