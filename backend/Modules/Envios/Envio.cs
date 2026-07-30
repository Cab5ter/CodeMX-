namespace CodeMX.Api.Modules.Envios;

/// <summary>Entidad de dominio del módulo Envíos.</summary>
public class Envio
{
    public long Id { get; set; }
    public long UsuarioId { get; set; }
    public long RetoId { get; set; }
    public string CodigoFuente { get; set; } = "";
    public Veredicto Veredicto { get; set; } = Veredicto.PENDIENTE;
    public DateTime EnviadoEn { get; set; } = DateTime.UtcNow;
}
