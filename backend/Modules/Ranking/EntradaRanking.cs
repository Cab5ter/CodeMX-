namespace CodeMX.Api.Modules.Ranking;

/// <summary>Entrada de la tabla de posiciones de un usuario.</summary>
public class EntradaRanking
{
    public long Id { get; set; }
    public long UsuarioId { get; set; }
    public int PuntajeTotal { get; set; }
    public int RetosResueltos { get; set; }
    public DateTime ActualizadoEn { get; set; } = DateTime.UtcNow;
}
