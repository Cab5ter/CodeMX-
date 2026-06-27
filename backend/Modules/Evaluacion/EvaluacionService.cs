using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>
/// Contexto del patrón Strategy. Obtiene los casos de prueba del reto (vía IRetosApi) y
/// delega la ejecución a una estrategia creada por el Factory Method: intenta primero la
/// estrategia remota (Servicio Python) y, si falla, recurre a la estrategia local.
/// </summary>
public class EvaluacionService : IEvaluacionApi
{
    private readonly IEvaluadorStrategyFactory _factory;
    private readonly IRetosApi _retos;

    public EvaluacionService(IEvaluadorStrategyFactory factory, IRetosApi retos)
    {
        _factory = factory;
        _retos = retos;
    }

    public async Task<ResultadoEvaluacion> EvaluarAsync(SolicitudEvaluacion solicitud)
    {
        var casos = await _retos.ObtenerCasosAsync(solicitud.RetoId);

        try
        {
            // Estrategia preferida: ejecución remota en el Servicio Python.
            return await _factory.Crear(TipoEvaluacion.Remota).EjecutarAsync(solicitud, casos);
        }
        catch
        {
            // Respaldo: ejecución local mientras el Servicio Python no esté disponible.
            return await _factory.Crear(TipoEvaluacion.Local).EjecutarAsync(solicitud, casos);
        }
    }
}
