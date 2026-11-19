namespace CodeMX.Api.Modules.Usuarios;

/// <summary>Implementación del módulo Usuarios.</summary>
public class UsuarioService : IUsuariosApi
{
    private readonly UsuarioRepository _repo;

    public UsuarioService(UsuarioRepository repo) => _repo = repo;

    public Task<List<Usuario>> ListarTodosAsync() => _repo.ListarTodos();

    public Task<Usuario?> BuscarPorIdAsync(long id) => _repo.BuscarPorId(id);

    public Task<Usuario?> BuscarPorEmailAsync(string email) => _repo.BuscarPorEmail(email);

    public async Task<Usuario?> AutenticarAsync(string email, string passwordHash)
    {
        var usuario = await _repo.BuscarPorEmail(email);
        return usuario is not null && usuario.PasswordHash == passwordHash ? usuario : null;
    }

    public Task<Usuario> GuardarAsync(Usuario usuario) => _repo.Guardar(usuario);

    public Task EliminarAsync(long id) => _repo.Eliminar(id);
}
