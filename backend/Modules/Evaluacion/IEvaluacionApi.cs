namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>
/// API pública del módulo Evaluación. La consume Envíos. Internamente envía el código
/// al Servicio Python (sandbox aislado) por HTTP y devuelve el resultado de las pruebas.
/// </summary>
public interface IEvaluacionApi
{
    Task<ResultadoEvaluacion> EvaluarAsync(SolicitudEvaluacion solicitud);
}
