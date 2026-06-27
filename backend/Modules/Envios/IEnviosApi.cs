namespace CodeMX.Api.Modules.Envios;

/// <summary>
/// API pública del módulo Envíos. La consume el gateway cuando el estudiante manda
/// una solución. Internamente coordina con Evaluación y Ranking vía sus interfaces.
/// </summary>
public interface IEnviosApi
{
    Task<Envio> EnviarAsync(Envio envio);
    Task<Envio?> BuscarPorIdAsync(long id);
    Task<List<Envio>> ListarPorUsuarioAsync(long usuarioId);
    Task<List<Envio>> ListarPorRetoAsync(long retoId);
}
