namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>
/// Patrón <b>Factory Method</b> (GoF). Encapsula la creación de la estrategia de evaluación
/// concreta según el <see cref="TipoEvaluacion"/> solicitado, de modo que el contexto
/// (EvaluacionService) no dependa de las clases concretas de cada estrategia.
/// </summary>
public interface IEvaluadorStrategyFactory
{
    IEvaluacionStrategy Crear(TipoEvaluacion tipo);
}

public class EvaluadorStrategyFactory : IEvaluadorStrategyFactory
{
    private readonly EvaluacionRemotaStrategy _remota;
    private readonly EvaluacionLocalStrategy _local;

    public EvaluadorStrategyFactory(EvaluacionRemotaStrategy remota, EvaluacionLocalStrategy local)
    {
        _remota = remota;
        _local = local;
    }

    public IEvaluacionStrategy Crear(TipoEvaluacion tipo) => tipo switch
    {
        TipoEvaluacion.Remota => _remota,
        TipoEvaluacion.Local => _local,
        _ => _local
    };
}
