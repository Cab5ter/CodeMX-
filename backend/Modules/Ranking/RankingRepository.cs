using CodeMX.Api.Persistence;
using Microsoft.EntityFrameworkCore;

namespace CodeMX.Api.Modules.Ranking;

/// <summary>Acceso a datos del módulo Ranking.</summary>
public class RankingRepository
{
    private readonly CodeMxDbContext _db;

    public RankingRepository(CodeMxDbContext db) => _db = db;

    public Task<List<EntradaRanking>> ObtenerOrdenado() =>
        _db.Ranking.OrderByDescending(r => r.PuntajeTotal).ToListAsync();

    public Task<EntradaRanking?> BuscarPorUsuario(long usuarioId) =>
        _db.Ranking.FirstOrDefaultAsync(r => r.UsuarioId == usuarioId);

    public async Task<EntradaRanking> Guardar(EntradaRanking entrada)
    {
        if (entrada.Id == 0) _db.Ranking.Add(entrada);
        else _db.Ranking.Update(entrada);
        await _db.SaveChangesAsync();
        return entrada;
    }
}
