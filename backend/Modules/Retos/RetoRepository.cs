using CodeMX.Api.Persistence;
using Microsoft.EntityFrameworkCore;

namespace CodeMX.Api.Modules.Retos;

/// <summary>Acceso a datos del módulo Retos (retos y sus casos de prueba).</summary>
public class RetoRepository
{
    private readonly CodeMxDbContext _db;

    public RetoRepository(CodeMxDbContext db) => _db = db;

    public Task<List<Reto>> ListarTodos() => _db.Retos.OrderBy(r => r.Id).ToListAsync();

    public Task<List<Reto>> ListarPorDificultad(Dificultad dificultad) =>
        _db.Retos.Where(r => r.Dificultad == dificultad).ToListAsync();

    public Task<Reto?> BuscarPorId(long id) => _db.Retos.FirstOrDefaultAsync(r => r.Id == id);

    public Task<Reto?> BuscarPorTitulo(string titulo) => _db.Retos.FirstOrDefaultAsync(r => r.Titulo == titulo);

    public Task<List<CasoPrueba>> ObtenerCasos(long retoId) =>
        _db.CasosPrueba.Where(c => c.RetoId == retoId).ToListAsync();

    public async Task<Reto> Guardar(Reto reto)
    {
        if (reto.Id == 0) _db.Retos.Add(reto);
        else _db.Retos.Update(reto);
        await _db.SaveChangesAsync();
        return reto;
    }

    public async Task Eliminar(long id)
    {
        var reto = await _db.Retos.FindAsync(id);
        if (reto is not null)
        {
            _db.Retos.Remove(reto);
            await _db.SaveChangesAsync();
        }
    }
}
