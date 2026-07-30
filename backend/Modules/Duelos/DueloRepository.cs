using CodeMX.Api.Persistence;
using Microsoft.EntityFrameworkCore;

namespace CodeMX.Api.Modules.Duelos;

/// <summary>Acceso a datos del módulo Duelos.</summary>
public class DueloRepository
{
    private readonly CodeMxDbContext _db;

    public DueloRepository(CodeMxDbContext db) => _db = db;

    public async Task<Duelo> Guardar(Duelo duelo)
    {
        if (duelo.Id == 0) _db.Duelos.Add(duelo);
        else _db.Duelos.Update(duelo);
        await _db.SaveChangesAsync();
        return duelo;
    }

    public Task<Duelo?> BuscarPorId(long id) => _db.Duelos.FirstOrDefaultAsync(d => d.Id == id);

    public Task<List<Duelo>> ListarPorUsuario(long usuarioId) =>
        _db.Duelos
           .Where(d => d.Jugador1Id == usuarioId || d.Jugador2Id == usuarioId)
           .OrderByDescending(d => d.CreadoEn)
           .ToListAsync();
}
