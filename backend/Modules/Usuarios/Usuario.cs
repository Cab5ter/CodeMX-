using System.Text.Json.Serialization;

namespace CodeMX.Api.Modules.Usuarios;

/// <summary>Entidad de dominio del módulo Usuarios.</summary>
public class Usuario
{
    public long Id { get; set; }
    public string Nombre { get; set; } = "";
    public string Email { get; set; } = "";

    /// <summary>
    /// Hash BCrypt de la contraseña. Nunca sale de la API: el atributo lo excluye de
    /// cualquier respuesta JSON aunque la entidad se serialice por descuido (DT-01).
    /// </summary>
    [JsonIgnore]
    public string PasswordHash { get; set; } = "";

    public DateTime CreadoEn { get; set; } = DateTime.UtcNow;
}
