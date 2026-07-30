using CodeMX.Api.Persistence;
using Microsoft.EntityFrameworkCore;

namespace CodeMX.Api.Modules.Envios;

/// <summary>Acceso a datos del módulo Envíos.</summary>
public class EnvioRepository
{
    private readonly CodeMxDbContext _db;

    public EnvioRepository(CodeMxDbContext db) => _db = db;

    public async Task<Envio> Guardar(Envio envio)
    {
        if (envio.Id == 0) _db.Envios.Add(envio);
        else _db.Envios.Update(envio);
        await _db.SaveChangesAsync();
        return envio;
    }

    public Task<Envio?> BuscarPorId(long id) => _db.Envios.FirstOrDefaultAsync(e => e.Id == id);

    public Task<List<Envio>> ListarPorUsuario(long usuarioId) =>
        _db.Envios.Where(e => e.UsuarioId == usuarioId).ToListAsync();

    public Task<List<Envio>> ListarPorReto(long retoId) =>
        _db.Envios.Where(e => e.RetoId == retoId).ToListAsync();

    public Task<bool> TieneAceptado(long usuarioId, long retoId) =>
        _db.Envios.AnyAsync(e => e.UsuarioId == usuarioId && e.RetoId == retoId && e.Veredicto == Veredicto.ACEPTADO);
}
