namespace CodeMX.Api.Modules.Cursos;

/// <summary>Módulo de aprendizaje (curso).</summary>
public class Modulo
{
    public long Id { get; set; }
    public string Titulo { get; set; } = "";
    public string Descripcion { get; set; } = "";
    public string Icono { get; set; } = "";
    public int Orden { get; set; }
}
