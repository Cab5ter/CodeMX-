using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>
/// Patrón <b>Strategy</b> (GoF). Define el algoritmo intercambiable para ejecutar el código
/// de un envío contra sus casos de prueba. Cada estrategia concreta resuelve el "cómo"
/// (remoto vía Servicio Python, o local vía proceso) sin que el resto del sistema cambie.
/// </summary>
public interface IEvaluacionStrategy
{
    Task<ResultadoEvaluacion> EjecutarAsync(SolicitudEvaluacion solicitud, List<CasoPrueba> casos);
}
