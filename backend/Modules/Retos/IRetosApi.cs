namespace CodeMX.Api.Modules.Retos;

/// <summary>
/// API pública del módulo Retos. La consumen Ranking (puntos por reto) y
/// Evaluación (casos de prueba). Ningún módulo accede a su repositorio directamente.
/// </summary>
public interface IRetosApi
{
    Task<List<Reto>> ListarTodosAsync();
    Task<List<Reto>> ListarPorDificultadAsync(Dificultad dificultad);
    Task<Reto?> BuscarPorIdAsync(long id);
    Task<CasoPrueba?> ObtenerEjemploAsync(long retoId);

    /// <summary>Casos de prueba de un reto. Lo usa el módulo Evaluación.</summary>
    Task<List<CasoPrueba>> ObtenerCasosAsync(long retoId);

    /// <summary>Puntos que otorga un reto según su dificultad. Lo usa el módulo Ranking.</summary>
    Task<int> PuntosPorRetoAsync(long retoId);

    Task<Reto> GuardarAsync(Reto reto);
    Task EliminarAsync(long id);
}
