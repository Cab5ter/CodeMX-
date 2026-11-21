package mx.codemx.modules.evaluacion;

/**
 * Patrón <b>Factory Method</b> (GoF). Encapsula la creación de la estrategia de evaluación
 * concreta según el {@link TipoEvaluacion} solicitado, de modo que el contexto
 * (EvaluacionService) no dependa de las clases concretas de cada estrategia.
 */
public interface EvaluadorStrategyFactory {

    EvaluacionStrategy crear(TipoEvaluacion tipo);
}
