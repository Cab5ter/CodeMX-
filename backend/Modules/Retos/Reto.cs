namespace CodeMX.Api.Modules.Retos;

/// <summary>Entidad de dominio del módulo Retos.</summary>
public class Reto
{
    public long Id { get; set; }
    public string Titulo { get; set; } = "";
    public string Descripcion { get; set; } = "";
    public Dificultad Dificultad { get; set; }
    public DateTime CreadoEn { get; set; } = DateTime.UtcNow;
}
