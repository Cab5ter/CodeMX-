using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>
/// Implementación del módulo Evaluación. Obtiene los casos de prueba del reto
/// (vía IRetosApi) y delega la ejecución del código al Servicio Python (RunnerClient).
/// </summary>
public class EvaluacionService : IEvaluacionApi
{
    private readonly RunnerClient _runner;
    private readonly IRetosApi _retos;

    public EvaluacionService(RunnerClient runner, IRetosApi retos)
    {
        _runner = runner;
        _retos = retos;
    }

    public async Task<ResultadoEvaluacion> EvaluarAsync(SolicitudEvaluacion solicitud)
    {
        var casos = await _retos.ObtenerCasosAsync(solicitud.RetoId);
        return await _runner.EjecutarAsync(solicitud, casos);
    }
}
