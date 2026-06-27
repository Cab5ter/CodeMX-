namespace CodeMX.Api.Modules.Cursos;

/// <summary>Pregunta de opción múltiple del examen de un módulo.</summary>
public class PreguntaExamen
{
    public long Id { get; set; }
    public long ModuloId { get; set; }
    public string Enunciado { get; set; } = "";
    public string OpcionA { get; set; } = "";
    public string OpcionB { get; set; } = "";
    public string OpcionC { get; set; } = "";
    public string OpcionD { get; set; } = "";

    // Índice de la opción correcta (0 = A, 1 = B, 2 = C, 3 = D).
    public int Correcta { get; set; }

    public int Orden { get; set; }
}
