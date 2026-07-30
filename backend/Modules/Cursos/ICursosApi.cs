namespace CodeMX.Api.Modules.Cursos;

/// <summary>
/// API pública del módulo Cursos (plataforma de aprendizaje). La consume el gateway.
/// Internamente consulta al módulo Envíos (IEnviosApi) para saber si un ejercicio fue resuelto.
/// </summary>
public interface ICursosApi
{
    Task<List<ModuloResumen>> ListarModulosAsync(long? usuarioId);
    Task<ModuloDetalle> ObtenerModuloAsync(long moduloId, long? usuarioId);
    Task<LeccionDetalle> ObtenerLeccionAsync(long leccionId, long? usuarioId);
    Task CompletarLeccionAsync(long leccionId, long usuarioId);
    Task<List<PreguntaVista>> ObtenerExamenAsync(long moduloId, long usuarioId);
    Task<ResultadoExamen> CalificarExamenAsync(long moduloId, long usuarioId, Dictionary<long, int> respuestas);
}
