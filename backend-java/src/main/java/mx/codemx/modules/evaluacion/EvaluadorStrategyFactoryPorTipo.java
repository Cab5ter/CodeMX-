package mx.codemx.modules.evaluacion;

import org.springframework.stereotype.Component;

@Component
public class EvaluadorStrategyFactoryPorTipo implements EvaluadorStrategyFactory {

    private final EvaluacionRemotaStrategy remota;
    private final EvaluacionLocalStrategy local;

    public EvaluadorStrategyFactoryPorTipo(EvaluacionRemotaStrategy remota, EvaluacionLocalStrategy local) {
        this.remota = remota;
        this.local = local;
    }

    @Override
    public EvaluacionStrategy crear(TipoEvaluacion tipo) {
        return switch (tipo) {
            case REMOTA -> remota;
            case LOCAL -> local;
        };
    }
}
