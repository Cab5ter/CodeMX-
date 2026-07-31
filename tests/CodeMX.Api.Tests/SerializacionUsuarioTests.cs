using System.Text.Json;
using CodeMX.Api.Modules.Usuarios;

namespace CodeMX.Api.Tests;

/// <summary>
/// Guardia de regresión de DT-01: el hash de la contraseña no debe aparecer en el JSON
/// que sale de la API, ni siquiera si alguien serializa la entidad por descuido.
/// </summary>
public class SerializacionUsuarioTests
{
    private static readonly JsonSerializerOptions Opciones = new(JsonSerializerDefaults.Web);

    [Fact]
    public void La_entidad_Usuario_no_serializa_el_hash()
    {
        // Arrange
        var usuario = new Usuario
        {
            Id = 7,
            Nombre = "Leonardo",
            Email = "leo@codemx.mx",
            PasswordHash = "$2a$11$hashquenodebesalir"
        };

        // Act
        var json = JsonSerializer.Serialize(usuario, Opciones);

        // Assert
        Assert.DoesNotContain("passwordHash", json, StringComparison.OrdinalIgnoreCase);
        Assert.DoesNotContain("$2a$11$hashquenodebesalir", json);
        Assert.Contains("leo@codemx.mx", json);
    }

    [Fact]
    public void El_UsuarioDto_expone_solo_los_campos_publicos()
    {
        // Arrange
        var usuario = new Usuario
        {
            Id = 7,
            Nombre = "Leonardo",
            Email = "leo@codemx.mx",
            PasswordHash = "$2a$11$hashquenodebesalir"
        };

        // Act
        var json = JsonSerializer.Serialize(UsuarioDto.De(usuario), Opciones);

        // Assert
        Assert.DoesNotContain("password", json, StringComparison.OrdinalIgnoreCase);
        Assert.Contains("\"id\":7", json);
        Assert.Contains("Leonardo", json);
    }
}
