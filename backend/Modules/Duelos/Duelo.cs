using CodeMX.Api.Modules.Retos;

namespace CodeMX.Api.Modules.Duelos;

/// <summary>
/// Entidad de dominio del módulo Duelos. Registra un enfrentamiento 1 vs 1 entre dos
/// usuarios sobre un problema de programación. El enunciado y los casos de prueba del
/// problema viven en memoria mientras el duelo está en curso (módulo de tiempo real);
/// aquí sólo persiste lo necesario para el ranking y el historial.
/// </summary>
public class Duelo
{
    public long Id { get; set; }
    public long Jugador1Id { get; set; }
    public long Jugador2Id { get; set; }

    /// <summary>Quién resolvió primero. <c>null</c> mientras el duelo sigue en curso.</summary>
    public long? GanadorId { get; set; }

    public string Titulo { get; set; } = "";

    /// <summary>Dificultad elegida por ambos jugadores; define los puntos en juego.</summary>
    public Dificultad Dificultad { get; set; } = Dificultad.INTERMEDIO;

    public EstadoDuelo Estado { get; set; } = EstadoDuelo.EnCurso;

    public DateTime CreadoEn { get; set; } = DateTime.UtcNow;
    public DateTime? TerminadoEn { get; set; }
}
