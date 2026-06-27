namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>Resultado que el Servicio Python devuelve hacia Envíos.</summary>
public record ResultadoEvaluacion(string Veredicto, string? MensajeError, long TiempoEjecucionMs);
