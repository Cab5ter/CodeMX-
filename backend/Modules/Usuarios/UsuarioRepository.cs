using CodeMX.Api.Persistence;
using Microsoft.EntityFrameworkCore;

namespace CodeMX.Api.Modules.Usuarios;

/// <summary>Acceso a datos del módulo Usuarios (equivalente al repository de Spring Data).</summary>
public class UsuarioRepository
{
    private readonly CodeMxDbContext _db;

    public UsuarioRepository(CodeMxDbContext db) => _db = db;

    public Task<List<Usuario>> ListarTodos() => _db.Usuarios.ToListAsync();

    public Task<Usuario?> BuscarPorId(long id) => _db.Usuarios.FirstOrDefaultAsync(u => u.Id == id);

    public Task<Usuario?> BuscarPorEmail(string email) => _db.Usuarios.FirstOrDefaultAsync(u => u.Email == email);

    public async Task<Usuario> Guardar(Usuario usuario)
    {
        if (usuario.Id == 0) _db.Usuarios.Add(usuario);
        else _db.Usuarios.Update(usuario);
        await _db.SaveChangesAsync();
        return usuario;
    }

    public async Task Eliminar(long id)
    {
        var usuario = await _db.Usuarios.FindAsync(id);
        if (usuario is not null)
        {
            _db.Usuarios.Remove(usuario);
            await _db.SaveChangesAsync();
        }
    }
}
