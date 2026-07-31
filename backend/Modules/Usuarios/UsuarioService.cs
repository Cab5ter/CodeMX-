namespace CodeMX.Api.Modules.Usuarios;

/// <summary>Implementación del módulo Usuarios.</summary>
public class UsuarioService : IUsuariosApi
{
    /// <summary>
    /// Coste de trabajo de BCrypt. Cada +1 duplica el tiempo de cómputo por hash.
    /// Es el punto de sensibilidad SP-01 del ATAM: sube la resistencia a fuerza bruta
    /// a costa de latencia en login y registro.
    /// </summary>
    private const int CostoBCrypt = 11;

    private const int LongitudMinimaPassword = 8;

    private readonly UsuarioRepository _repo;

    public UsuarioService(UsuarioRepository repo) => _repo = repo;

    public Task<List<Usuario>> ListarTodosAsync() => _repo.ListarTodos();

    public Task<Usuario?> BuscarPorIdAsync(long id) => _repo.BuscarPorId(id);

    public Task<Usuario?> BuscarPorEmailAsync(string email) => _repo.BuscarPorEmail(email);

    public async Task<Usuario> RegistrarAsync(RegistroRequest registro)
    {
        var nombre = (registro.Nombre ?? "").Trim();
        var email = NormalizarEmail(registro.Email);
        var password = registro.Password ?? "";

        if (nombre.Length == 0)
            throw new RegistroInvalidoException("El nombre no puede ir vacío.");
        if (email.Length == 0 || !email.Contains('@'))
            throw new RegistroInvalidoException("El correo no es válido.");
        if (password.Length < LongitudMinimaPassword)
            throw new RegistroInvalidoException(
                $"La contraseña debe tener al menos {LongitudMinimaPassword} caracteres.");

        if (await _repo.BuscarPorEmail(email) is not null)
            throw new EmailYaRegistradoException(email);

        var usuario = new Usuario
        {
            Nombre = nombre,
            Email = email,
            PasswordHash = BCrypt.Net.BCrypt.HashPassword(password, CostoBCrypt),
            CreadoEn = DateTime.UtcNow
        };

        return await _repo.Guardar(usuario);
    }

    public async Task<Usuario?> AutenticarAsync(string email, string password)
    {
        var usuario = await _repo.BuscarPorEmail(NormalizarEmail(email));

        // Se verifica siempre contra un hash —el del usuario o uno señuelo— para que el
        // tiempo de respuesta no revele si el correo existe (enumeración de cuentas).
        var hash = usuario?.PasswordHash is { Length: > 0 } h ? h : HashSenuelo;

        var coincide = Verificar(password ?? "", hash);
        return usuario is not null && coincide ? usuario : null;
    }

    public Task EliminarAsync(long id) => _repo.Eliminar(id);

    private static string NormalizarEmail(string? email) => (email ?? "").Trim().ToLowerInvariant();

    /// <summary>Hash de una contraseña arbitraria, usado para igualar tiempos cuando el correo no existe.</summary>
    private static readonly string HashSenuelo =
        BCrypt.Net.BCrypt.HashPassword("cuenta-inexistente", CostoBCrypt);

    /// <summary>
    /// Verifica contra un hash BCrypt. Un hash con formato inválido (por ejemplo, un
    /// registro viejo en texto plano anterior al ADR-07) se trata como no coincidente.
    /// </summary>
    private static bool Verificar(string password, string hash)
    {
        try
        {
            return BCrypt.Net.BCrypt.Verify(password, hash);
        }
        catch (BCrypt.Net.SaltParseException)
        {
            return false;
        }
    }
}
