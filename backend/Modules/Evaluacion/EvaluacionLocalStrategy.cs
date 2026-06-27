using System.Diagnostics;
using System.Text;
using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>
/// Estrategia concreta (Strategy): ejecuta el código localmente con un proceso python3,
/// respetando el mismo timeout. Sirve de respaldo mientras el Servicio Python externo
/// no esté disponible.
/// </summary>
public class EvaluacionLocalStrategy : IEvaluacionStrategy
{
    private readonly int _timeoutMs;

    public EvaluacionLocalStrategy(IConfiguration config)
    {
        _timeoutMs = int.TryParse(config["Evaluador:TimeoutMs"], out var t) ? t : 5000;
    }

    public Task<ResultadoEvaluacion> EjecutarAsync(SolicitudEvaluacion solicitud, List<CasoPrueba> casos)
        => Task.FromResult(Ejecutar(solicitud.CodigoFuente, casos));

    private ResultadoEvaluacion Ejecutar(string codigo, List<CasoPrueba> casos)
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
                    return new ResultadoEvaluacion("INCORRECTO",
                        "Salida incorrecta para la entrada: " + (caso.InputData ?? "").Replace("\n", " | "),
                        (long)(DateTime.UtcNow - inicio).TotalMilliseconds);
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
