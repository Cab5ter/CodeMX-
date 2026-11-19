using System.Collections.Concurrent;
using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.RealTime;

/// <summary>Un jugador esperando rival en la cola de emparejamiento, con la dificultad que eligió.</summary>
public record JugadorEnEspera(string ConnectionId, long UsuarioId, string Nombre, Dificultad Dificultad);

/// <summary>
/// Estado en memoria de un duelo en curso: conexiones, jugadores, el problema (con sus
/// casos de prueba, que jamás salen del servidor) y quién ganó. Vive mientras dura el duelo.
/// </summary>
public class DueloActivo
{
    public long DueloId { get; init; }
    public string Grupo => $"duelo-{DueloId}";

    public JugadorEnEspera Jugador1 { get; init; } = default!;
    public JugadorEnEspera Jugador2 { get; init; } = default!;
    public Modules.Duelos.ProblemaDuelo Problema { get; init; } = default!;

    public long? GanadorId { get; private set; }

    /// <summary>Fija al ganador de forma atómica. Devuelve true sólo la primera vez.</summary>
    public bool IntentarGanar(long usuarioId)
    {
        lock (this)
        {
            if (GanadorId is not null) return false;
            GanadorId = usuarioId;
            return true;
        }
    }

    public string NombreDe(long usuarioId) =>
        usuarioId == Jugador1.UsuarioId ? Jugador1.Nombre : Jugador2.Nombre;
}

/// <summary>
/// Servicio singleton que gestiona la cola de emparejamiento y los duelos activos en memoria.
/// El monolito corre en una sola instancia, así que un estado en memoria con bloqueo basta.
/// El emparejamiento sólo junta a jugadores que eligieron la <b>misma dificultad</b>.
/// </summary>
public class MatchmakingService
{
    private readonly object _lock = new();
    private readonly List<JugadorEnEspera> _espera = new();
    private readonly ConcurrentDictionary<long, DueloActivo> _activos = new();

    /// <summary>
    /// Mete al jugador a la sala de espera; si ya había alguien (distinto, misma dificultad)
    /// esperando, lo empareja y devuelve al rival. Devuelve <c>null</c> si quedó en espera.
    /// </summary>
    public JugadorEnEspera? EmparejarOEncolar(JugadorEnEspera jugador)
    {
        lock (_lock)
        {
            // Evita duplicados de la misma conexión o usuario en la cola.
            _espera.RemoveAll(j => j.ConnectionId == jugador.ConnectionId || j.UsuarioId == jugador.UsuarioId);

            int idx = _espera.FindIndex(j => j.UsuarioId != jugador.UsuarioId && j.Dificultad == jugador.Dificultad);
            if (idx >= 0)
            {
                var rival = _espera[idx];
                _espera.RemoveAt(idx);
                return rival; // emparejado
            }

            _espera.Add(jugador);
            return null; // en espera
        }
    }

    public void RegistrarDuelo(DueloActivo duelo) => _activos[duelo.DueloId] = duelo;

    public DueloActivo? Obtener(long dueloId) =>
        _activos.TryGetValue(dueloId, out var d) ? d : null;

    public void Quitar(long dueloId) => _activos.TryRemove(dueloId, out _);

    /// <summary>
    /// Saca una conexión de la cola y/o de su duelo activo (al desconectarse). Devuelve el
    /// duelo activo y aún no terminado en el que estaba, para que el hub avise al rival.
    /// </summary>
    public DueloActivo? QuitarConexion(string connectionId)
    {
        lock (_lock)
        {
            _espera.RemoveAll(j => j.ConnectionId == connectionId);
        }

        foreach (var duelo in _activos.Values)
        {
            bool participa = duelo.Jugador1.ConnectionId == connectionId
                          || duelo.Jugador2.ConnectionId == connectionId;
            if (participa && duelo.GanadorId is null)
                return duelo;
        }
        return null;
    }
}
