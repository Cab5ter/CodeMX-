package mx.codemx.modules.evaluacion;

public interface EvaluadorStrategyFactory {

    EvaluacionStrategy crear(TipoEvaluacion tipo);
}
