namespace CodeMX.Api.Modules.Retos;

/// <summary>Implementación del módulo Retos.</summary>
public class RetoService : IRetosApi
{
    private readonly RetoRepository _repo;

    public RetoService(RetoRepository repo) => _repo = repo;

    public Task<List<Reto>> ListarTodosAsync() => _repo.ListarTodos();

    public Task<List<Reto>> ListarPorDificultadAsync(Dificultad dificultad) => _repo.ListarPorDificultad(dificultad);

    public Task<Reto?> BuscarPorIdAsync(long id) => _repo.BuscarPorId(id);

    public async Task<CasoPrueba?> ObtenerEjemploAsync(long retoId) =>
        (await _repo.ObtenerCasos(retoId)).FirstOrDefault();

    public Task<List<CasoPrueba>> ObtenerCasosAsync(long retoId) => _repo.ObtenerCasos(retoId);

    public async Task<int> PuntosPorRetoAsync(long retoId)
    {
        var reto = await _repo.BuscarPorId(retoId);
        return reto?.Dificultad switch
        {
            Dificultad.BASICO => 10,
            Dificultad.INTERMEDIO => 25,
            Dificultad.AVANZADO => 50,
            _ => 10
        };
    }

    public Task<Reto> GuardarAsync(Reto reto) => _repo.Guardar(reto);

    public Task EliminarAsync(long id) => _repo.Eliminar(id);
}
