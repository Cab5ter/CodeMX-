using CodeMX.Api.Modules.Evaluacion;
using CodeMX.Api.Modules.Ranking;
using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Duelos;

/// <summary>
/// Implementación del módulo Duelos. Coordina persistencia, evaluación y ranking:
///   - reutiliza el Factory + Strategy del módulo Evaluación (estrategia local) para
///     calificar el código contra los casos del problema;
///   - delega los puntos al módulo Ranking por su interfaz pública (nunca a su repositorio).
/// </summary>
public class DueloService : IDuelosApi
{
    /// <summary>
    /// Puntos en juego según la dificultad: más fácil, menos se gana y menos se pierde;
    /// más difícil, más se gana pero también más se pierde.
    /// </summary>
    public static (int Ganar, int Perder) PuntosPorDificultad(Dificultad dificultad) => dificultad switch
    {
        Dificultad.BASICO    => (15, 5),
        Dificultad.INTERMEDIO => (25, 10),
        Dificultad.AVANZADO  => (40, 20),
        _ => (25, 10)
    };

    private readonly DueloRepository _repo;
    private readonly IEvaluadorStrategyFactory _evaluadores;
    private readonly IRankingApi _ranking;

    public DueloService(DueloRepository repo, IEvaluadorStrategyFactory evaluadores, IRankingApi ranking)
    {
        _repo = repo;
        _evaluadores = evaluadores;
        _ranking = ranking;
    }

    public Task<Duelo> CrearAsync(long jugador1Id, long jugador2Id, string titulo, Dificultad dificultad) =>
        _repo.Guardar(new Duelo
        {
            Jugador1Id = jugador1Id,
            Jugador2Id = jugador2Id,
            Titulo = titulo,
            Dificultad = dificultad,
            Estado = EstadoDuelo.EnCurso,
            CreadoEn = DateTime.UtcNow
        });

    public Task<ResultadoEvaluacion> EvaluarAsync(string codigoFuente, List<CasoPrueba> casos)
    {
        // RetoId = 0 porque los casos vienen del problema del duelo, no de un reto persistido.
        var estrategia = _evaluadores.Crear(TipoEvaluacion.Local);
        return estrategia.EjecutarAsync(new SolicitudEvaluacion(0, codigoFuente), casos);
    }

    public async Task RegistrarGanadorAsync(long dueloId, long ganadorId)
    {
        var duelo = await _repo.BuscarPorId(dueloId);
        if (duelo is null || duelo.Estado == EstadoDuelo.Terminado) return; // idempotente

        long perdedorId = duelo.Jugador1Id == ganadorId ? duelo.Jugador2Id : duelo.Jugador1Id;

        duelo.GanadorId = ganadorId;
        duelo.Estado = EstadoDuelo.Terminado;
        duelo.TerminadoEn = DateTime.UtcNow;
        await _repo.Guardar(duelo);

        var (ganar, perder) = PuntosPorDificultad(duelo.Dificultad);
        await _ranking.RegistrarResultadoDueloAsync(ganadorId, perdedorId, ganar, perder);
    }

    public Task<List<Duelo>> HistorialAsync(long usuarioId) => _repo.ListarPorUsuario(usuarioId);
}
