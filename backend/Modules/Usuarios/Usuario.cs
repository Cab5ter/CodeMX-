namespace CodeMX.Api.Modules.Usuarios;

/// <summary>Entidad de dominio del módulo Usuarios.</summary>
public class Usuario
{
    public long Id { get; set; }
    public string Nombre { get; set; } = "";
    public string Email { get; set; } = "";
    public string PasswordHash { get; set; } = "";
    public DateTime CreadoEn { get; set; } = DateTime.UtcNow;
}
