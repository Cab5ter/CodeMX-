using System.Net.Http.Json;
using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>
/// Estrategia concreta (Strategy): envía el código al Servicio Python externo (sandbox)
/// por HTTP/JSON con un timeout explícito. Si el servicio no responde, lanza excepción
/// para que el contexto (EvaluacionService) recurra a la estrategia de respaldo.
/// </summary>
public class EvaluacionRemotaStrategy : IEvaluacionStrategy
{
    private readonly HttpClient _http;
    private readonly string _url;
    private readonly int _timeoutMs;

    public EvaluacionRemotaStrategy(HttpClient http, IConfiguration config)
    {
        _http = http;
        _url = config["Evaluador:Url"] ?? "http://localhost:8000";
        _timeoutMs = int.TryParse(config["Evaluador:TimeoutMs"], out var t) ? t : 5000;
    }

    public async Task<ResultadoEvaluacion> EjecutarAsync(SolicitudEvaluacion solicitud, List<CasoPrueba> casos)
    {
        using var cts = new CancellationTokenSource(_timeoutMs);
        var resp = await _http.PostAsJsonAsync($"{_url}/evaluar", solicitud, cts.Token);
        resp.EnsureSuccessStatusCode();
        return await resp.Content.ReadFromJsonAsync<ResultadoEvaluacion>(cancellationToken: cts.Token)
            ?? throw new InvalidOperationException("Respuesta vacía del Servicio Python");
    }
}
