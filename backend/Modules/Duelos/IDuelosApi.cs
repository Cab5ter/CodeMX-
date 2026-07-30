using CodeMX.Api.Modules.Evaluacion;
using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Duelos;

/// <summary>
/// API pública del módulo Duelos. La consumen el hub de tiempo real (para crear duelos,
/// evaluar soluciones y registrar al ganador) y el gateway (historial).
/// </summary>
public interface IDuelosApi
{
    /// <summary>Crea y persiste un duelo en curso entre dos jugadores.</summary>
    Task<Duelo> CrearAsync(long jugador1Id, long jugador2Id, string titulo, Dificultad dificultad);

    /// <summary>Ejecuta el código del jugador contra los casos del problema (motor local Python).</summary>
    Task<ResultadoEvaluacion> EvaluarAsync(string codigoFuente, List<CasoPrueba> casos);

    /// <summary>
    /// Marca al ganador del duelo, lo cierra y aplica los puntos en el ranking
    /// (+ al ganador, − al perdedor). Idempotente: si ya tenía ganador, no hace nada.
    /// </summary>
    Task RegistrarGanadorAsync(long dueloId, long ganadorId);

    Task<List<Duelo>> HistorialAsync(long usuarioId);
}
