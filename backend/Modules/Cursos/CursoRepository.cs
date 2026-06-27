using CodeMX.Api.Persistence;
using Microsoft.EntityFrameworkCore;

namespace CodeMX.Api.Modules.Cursos;

/// <summary>Acceso a datos del módulo Cursos.</summary>
public class CursoRepository
{
    private readonly CodeMxDbContext _db;

    public CursoRepository(CodeMxDbContext db) => _db = db;

    public Task<List<Modulo>> ListarModulos() => _db.Modulos.OrderBy(m => m.Orden).ToListAsync();

    public Task<Modulo?> BuscarModulo(long id) => _db.Modulos.FirstOrDefaultAsync(m => m.Id == id);

    public Task<List<Leccion>> LeccionesPorModulo(long moduloId) =>
        _db.Lecciones.Where(l => l.ModuloId == moduloId).OrderBy(l => l.Orden).ToListAsync();

    public Task<Leccion?> BuscarLeccion(long id) => _db.Lecciones.FirstOrDefaultAsync(l => l.Id == id);

    public Task<bool> ExisteProgreso(long usuarioId, long leccionId) =>
        _db.ProgresoLecciones.AnyAsync(p => p.UsuarioId == usuarioId && p.LeccionId == leccionId);

    public async Task GuardarProgreso(ProgresoLeccion progreso)
    {
        _db.ProgresoLecciones.Add(progreso);
        await _db.SaveChangesAsync();
    }

    public Task<List<PreguntaExamen>> PreguntasPorModulo(long moduloId) =>
        _db.PreguntasExamen.Where(p => p.ModuloId == moduloId).OrderBy(p => p.Orden).ToListAsync();
}
