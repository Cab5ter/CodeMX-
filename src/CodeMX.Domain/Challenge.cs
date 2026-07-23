namespace CodeMX.Domain;

/// <summary>
/// Representa un reto de programacion de la plataforma CodeMX.
/// Un reto tiene un titulo y un nivel de dificultad, y sabe
/// cuantos puntos base otorga segun esa dificultad.
/// </summary>
public class Challenge
{
    public string Title { get; }

    public Difficulty Difficulty { get; }

    public Challenge(string title, Difficulty difficulty)
    {
        if (string.IsNullOrWhiteSpace(title))
        {
            throw new ArgumentException(
                "El titulo del reto no puede estar vacio.", nameof(title));
        }

        Title = title.Trim();
        Difficulty = difficulty;
    }

    /// <summary>
    /// Puntos base que otorga el reto si se resuelve por completo.
    /// Escalan con la dificultad: facil 100, media 200, dificil 300.
    /// </summary>
    public int BasePoints() => Difficulty switch
    {
        Difficulty.Easy => 100,
        Difficulty.Medium => 200,
        Difficulty.Hard => 300,
        _ => 0
    };
}
