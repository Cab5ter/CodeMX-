using System.Diagnostics;
using System.Net.Http.Json;
using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>
/// Cliente del módulo Evaluación hacia el Servicio Python externo (sandbox de ejecución).
///
/// Según el diagrama del ADR-04, este es el único componente que se comunica con el
/// servicio Python por HTTP/JSON. La llamada usa un timeout explícito (Evaluador:TimeoutMs).
/// Mientras el servicio Python no esté disponible, cae a una ejecución local con un proceso
/// python3 que respeta el mismo timeout.
/// </summary>
public class RunnerClient
{
    private readonly HttpClient _http;
    private readonly string _evaluadorUrl;
    private readonly int _timeoutMs;

    public RunnerClient(HttpClient http, IConfiguration config)
    {
        _http = http;
        _evaluadorUrl = config["Evaluador:Url"] ?? "http://localhost:8000";
        _timeoutMs = int.TryParse(config["Evaluador:TimeoutMs"], out var t) ? t : 5000;
    }

    public async Task<ResultadoEvaluacion> EjecutarAsync(SolicitudEvaluacion solicitud, List<CasoPrueba> casos)
    {
        try
        {
            using var cts = new CancellationTokenSource(_timeoutMs);
            var resp = await _http.PostAsJsonAsync($"{_evaluadorUrl}/evaluar", solicitud, cts.Token);
            resp.EnsureSuccessStatusCode();
            var resultado = await resp.Content.ReadFromJsonAsync<ResultadoEvaluacion>(cancellationToken: cts.Token);
            if (resultado is not null) return resultado;
        }
        catch
        {
            // El Servicio Python no está disponible: se evalúa localmente.
        }

        return EjecutarLocalmente(solicitud.CodigoFuente, casos);
    }

    private ResultadoEvaluacion EjecutarLocalmente(string codigo, List<CasoPrueba> casos)
    {
        if (casos.Count == 0)
            return new ResultadoEvaluacion("ERROR_EN_EJECUCION", "El reto no tiene casos de prueba configurados", 0);

        var inicio = DateTime.UtcNow;

        foreach (var caso in casos)
        {
            try
            {
                var salida = EjecutarPython(codigo, caso.InputData ?? "");
                var esperado = (caso.OutputEsperado ?? "").Trim();

                if (salida != esperado)
                {
                    return new ResultadoEvaluacion("INCORRECTO",
                        "Salida incorrecta para la entrada: " + (caso.InputData ?? "").Replace("\n", " | "),
                        (long)(DateTime.UtcNow - inicio).TotalMilliseconds);
                }
            }
            catch (TimeoutException)
            {
                return new ResultadoEvaluacion("TIEMPO_LIMITE_EXCEDIDO", null, _timeoutMs);
            }
            catch (Exception e)
            {
                return new ResultadoEvaluacion("ERROR_EN_EJECUCION", e.Message,
                    (long)(DateTime.UtcNow - inicio).TotalMilliseconds);
            }
        }

        return new ResultadoEvaluacion("ACEPTADO", null, (long)(DateTime.UtcNow - inicio).TotalMilliseconds);
    }

    private string EjecutarPython(string codigo, string stdin)
    {
        var tempFile = Path.GetTempFileName();
        var pyFile = Path.ChangeExtension(tempFile, ".py");
        File.Move(tempFile, pyFile);
        try
        {
            File.WriteAllText(pyFile, codigo);

            var psi = new ProcessStartInfo
            {
                FileName = "python3",
                Arguments = pyFile,
                RedirectStandardInput = true,
                RedirectStandardOutput = true,
                RedirectStandardError = true,
                UseShellExecute = false
            };

            using var proc = Process.Start(psi)!;

            if (!string.IsNullOrWhiteSpace(stdin))
            {
                proc.StandardInput.Write(stdin);
                proc.StandardInput.Close();
            }

            var stdoutTask = proc.StandardOutput.ReadToEndAsync();
            var stderrTask = proc.StandardError.ReadToEndAsync();

            if (!proc.WaitForExit(_timeoutMs))
            {
                proc.Kill(entireProcessTree: true);
                throw new TimeoutException("TLE");
            }

            var stdout = stdoutTask.GetAwaiter().GetResult();
            var stderr = stderrTask.GetAwaiter().GetResult();

            if (proc.ExitCode != 0)
            {
                var primeraLinea = stderr
                    .Split('\n', StringSplitOptions.RemoveEmptyEntries)
                    .LastOrDefault(l => !l.StartsWith("  "))?.Trim()
                    ?? "Error en tiempo de ejecución";
                throw new Exception(primeraLinea);
            }

            return stdout.Trim();
        }
        finally
        {
            if (File.Exists(pyFile)) File.Delete(pyFile);
        }
    }
}
