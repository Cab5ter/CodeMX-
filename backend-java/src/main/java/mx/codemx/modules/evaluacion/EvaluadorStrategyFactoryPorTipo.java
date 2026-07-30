package mx.codemx.modules.evaluacion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EvaluadorStrategyFactoryPorTipo implements EvaluadorStrategyFactory {

    private final EvaluacionRemotaStrategy remota;
    private final EvaluacionLocalStrategy local;
    private final int limiteCaracteres;

    public EvaluadorStrategyFactoryPorTipo(
            EvaluacionRemotaStrategy remota,
            EvaluacionLocalStrategy local,
            @Value("${evaluador.limite-caracteres:20000}") int limiteCaracteres) {
        this.remota = remota;
        this.local = local;
        this.limiteCaracteres = limiteCaracteres;
    }

    @Override
    public EvaluacionStrategy crear(TipoEvaluacion tipo) {
        EvaluacionStrategy base = switch (tipo) {
            case REMOTA -> remota;
            case LOCAL -> local;
        };

        return new EvaluacionConValidacionDecorator(
                new EvaluacionConMetricasDecorator(base), limiteCaracteres);
    }
}
