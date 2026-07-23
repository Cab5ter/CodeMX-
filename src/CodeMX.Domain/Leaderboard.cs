namespace CodeMX.Domain;

/// <summary>
/// Tabla de posiciones (ranking) de CodeMX. Registra el mejor puntaje que
/// ha obtenido cada usuario y produce el orden de clasificacion.
/// </summary>
public class Leaderboard
{
    private readonly Dictionary<string, int> _bestScores = new();

    /// <summary>
    /// Registra el puntaje de un usuario. Solo conserva el mejor puntaje.
    /// </summary>
    public void Register(string user, int score)
    {
        if (string.IsNullOrWhiteSpace(user))
        {
            throw new ArgumentException(
                "El usuario no puede estar vacio.", nameof(user));
        }

        if (score < 0)
        {
            throw new ArgumentOutOfRangeException(
                nameof(score), "El puntaje no puede ser negativo.");
        }

        if (!_bestScores.TryGetValue(user, out int existing) || score > existing)
        {
            _bestScores[user] = score;
        }
    }

    /// <summary>Mejor puntaje registrado del usuario (0 si nunca ha participado).</summary>
    public int ScoreOf(string user)
        => _bestScores.TryGetValue(user, out int score) ? score : 0;

    /// <summary>
    /// Usuarios ordenados de mayor a menor puntaje. Ante un empate, se ordenan
    /// alfabeticamente para que el ranking sea determinista.
    /// </summary>
    public IReadOnlyList<string> Ranking()
        => _bestScores
            .OrderByDescending(entry => entry.Value)
            .ThenBy(entry => entry.Key, StringComparer.Ordinal)
            .Select(entry => entry.Key)
            .ToList();
}
