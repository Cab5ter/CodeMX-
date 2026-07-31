using CodeMX.Api.Modules.Usuarios;
using CodeMX.Api.Persistence;
using Microsoft.EntityFrameworkCore;

namespace CodeMX.Api.Tests;

/// <summary>
/// Pruebas del módulo Usuarios, centradas en el manejo de credenciales (ADR-07).
/// Cada prueba usa una base en memoria propia, así que no comparten estado.
/// </summary>
public class UsuarioServiceTests
{
    private static UsuarioService NuevoServicio()
    {
        var options = new DbContextOptionsBuilder<CodeMxDbContext>()
            .UseInMemoryDatabase($"codemx-{Guid.NewGuid()}")
            .Options;
        return new UsuarioService(new UsuarioRepository(new CodeMxDbContext(options)));
    }

    private static RegistroRequest Registro(
        string nombre = "Leonardo",
        string email = "leo@codemx.mx",
        string password = "contrasena123") => new(nombre, email, password);

    [Fact]
    public async Task Registrar_guarda_la_contrasena_hasheada_y_no_en_claro()
    {
        // Arrange
        var servicio = NuevoServicio();

        // Act
        var usuario = await servicio.RegistrarAsync(Registro(password: "superSecreta1"));

        // Assert
        Assert.NotEqual("superSecreta1", usuario.PasswordHash);
        Assert.StartsWith("$2", usuario.PasswordHash);          // prefijo de un hash BCrypt
        Assert.True(usuario.PasswordHash.Length >= 59);
    }

    [Fact]
    public async Task Registrar_genera_hashes_distintos_para_la_misma_contrasena()
    {
        // Arrange
        var servicio = NuevoServicio();

        // Act
        var a = await servicio.RegistrarAsync(Registro(email: "a@codemx.mx", password: "misma-clave"));
        var b = await servicio.RegistrarAsync(Registro(email: "b@codemx.mx", password: "misma-clave"));

        // Assert — cada hash lleva su propia sal, así que dos iguales serían un error
        Assert.NotEqual(a.PasswordHash, b.PasswordHash);
    }

    [Fact]
    public async Task Autenticar_acepta_la_contrasena_correcta()
    {
        // Arrange
        var servicio = NuevoServicio();
        await servicio.RegistrarAsync(Registro(password: "contrasena123"));

        // Act
        var usuario = await servicio.AutenticarAsync("leo@codemx.mx", "contrasena123");

        // Assert
        Assert.NotNull(usuario);
        Assert.Equal("leo@codemx.mx", usuario!.Email);
    }

    [Fact]
    public async Task Autenticar_rechaza_la_contrasena_incorrecta()
    {
        // Arrange
        var servicio = NuevoServicio();
        await servicio.RegistrarAsync(Registro(password: "contrasena123"));

        // Act
        var usuario = await servicio.AutenticarAsync("leo@codemx.mx", "otra-cosa");

        // Assert
        Assert.Null(usuario);
    }

    [Fact]
    public async Task Autenticar_devuelve_null_si_el_correo_no_existe()
    {
        // Arrange
        var servicio = NuevoServicio();

        // Act
        var usuario = await servicio.AutenticarAsync("nadie@codemx.mx", "loquesea");

        // Assert
        Assert.Null(usuario);
    }

    [Fact]
    public async Task Autenticar_ignora_mayusculas_y_espacios_en_el_correo()
    {
        // Arrange
        var servicio = NuevoServicio();
        await servicio.RegistrarAsync(Registro(email: "Leo@CodeMX.mx", password: "contrasena123"));

        // Act
        var usuario = await servicio.AutenticarAsync("  LEO@codemx.MX  ", "contrasena123");

        // Assert
        Assert.NotNull(usuario);
    }

    [Fact]
    public async Task Registrar_rechaza_un_correo_ya_usado()
    {
        // Arrange
        var servicio = NuevoServicio();
        await servicio.RegistrarAsync(Registro(email: "repetido@codemx.mx"));

        // Act + Assert
        await Assert.ThrowsAsync<EmailYaRegistradoException>(
            () => servicio.RegistrarAsync(Registro(email: "repetido@codemx.mx")));
    }

    [Theory]
    [InlineData("corta")]        // menos de 8 caracteres
    [InlineData("1234567")]
    [InlineData("")]
    public async Task Registrar_rechaza_contrasenas_demasiado_cortas(string password)
    {
        // Arrange
        var servicio = NuevoServicio();

        // Act + Assert
        await Assert.ThrowsAsync<RegistroInvalidoException>(
            () => servicio.RegistrarAsync(Registro(password: password)));
    }

    [Theory]
    [InlineData("", "leo@codemx.mx")]          // nombre vacío
    [InlineData("   ", "leo@codemx.mx")]
    [InlineData("Leonardo", "sin-arroba")]     // correo inválido
    [InlineData("Leonardo", "")]
    public async Task Registrar_rechaza_nombre_o_correo_invalidos(string nombre, string email)
    {
        // Arrange
        var servicio = NuevoServicio();

        // Act + Assert
        await Assert.ThrowsAsync<RegistroInvalidoException>(
            () => servicio.RegistrarAsync(Registro(nombre, email)));
    }
}
