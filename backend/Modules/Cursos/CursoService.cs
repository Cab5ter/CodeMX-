using CodeMX.Api.Modules.Envios;

namespace CodeMX.Api.Modules.Cursos;

/// <summary>
/// Módulo Cursos: organiza el aprendizaje en módulos con lecciones de teoría y ejercicios.
///
/// Reglas:
///  - Una lección de TEORIA se completa cuando el alumno la marca como leída.
///  - Una lección de EJERCICIO se completa cuando el alumno obtiene ACEPTADO en su reto
///    (se consulta al módulo Envíos vía IEnviosApi, sin duplicar estado).
///  - El examen se desbloquea al completar al menos UmbralExamen % de las lecciones.
/// </summary>
public class CursoService : ICursosApi
{
    public const int UmbralExamen = 70;       // % de lecciones para desbloquear el examen
    public const int UmbralAprobacion = 70;   // % de aciertos para aprobar

    private readonly CursoRepository _repo;
    private readonly IEnviosApi _envios;

    public CursoService(CursoRepository repo, IEnviosApi envios)
    {
        _repo = repo;
        _envios = envios;
    }

    public async Task<List<ModuloResumen>> ListarModulosAsync(long? usuarioId)
    {
        var modulos = await _repo.ListarModulos();
        var resumenes = new List<ModuloResumen>();

        foreach (var m in modulos)
        {
            var lecciones = await _repo.LeccionesPorModulo(m.Id);
            int total = lecciones.Count;
            int completadas = 0;
            foreach (var l in lecciones)
                if (await EstaCompletadaAsync(l, usuarioId)) completadas++;

            int progreso = total == 0 ? 0 : completadas * 100 / total;
            resumenes.Add(new ModuloResumen(m.Id, m.Titulo, m.Descripcion, m.Icono,
                total, completadas, progreso, progreso >= UmbralExamen));
        }
        return resumenes;
    }

    public async Task<ModuloDetalle> ObtenerModuloAsync(long moduloId, long? usuarioId)
    {
        var m = await _repo.BuscarModulo(moduloId)
            ?? throw new RecursoNoEncontradoException("Módulo no encontrado");

        var lecciones = await _repo.LeccionesPorModulo(moduloId);
        var vistas = new List<LeccionVista>();
        foreach (var l in lecciones)
            vistas.Add(new LeccionVista(l.Id, l.Titulo, l.Tipo, l.RetoId,
                await EstaCompletadaAsync(l, usuarioId), l.Orden));

        int total = vistas.Count;
        int completadas = vistas.Count(v => v.Completada);
        int progreso = total == 0 ? 0 : completadas * 100 / total;

        return new ModuloDetalle(m.Id, m.Titulo, m.Descripcion, m.Icono,
            vistas, total, completadas, progreso, UmbralExamen, progreso >= UmbralExamen);
    }

    public async Task<LeccionDetalle> ObtenerLeccionAsync(long leccionId, long? usuarioId)
    {
        var l = await _repo.BuscarLeccion(leccionId)
            ?? throw new RecursoNoEncontradoException("Lección no encontrada");
        var m = await _repo.BuscarModulo(l.ModuloId);
        string tituloModulo = m?.Titulo ?? "";
        return new LeccionDetalle(l.Id, l.ModuloId, tituloModulo, l.Titulo, l.Tipo,
            l.Contenido, l.EjemploCodigo, l.RetoId, await EstaCompletadaAsync(l, usuarioId));
    }

    public async Task CompletarLeccionAsync(long leccionId, long usuarioId)
    {
        var l = await _repo.BuscarLeccion(leccionId)
            ?? throw new RecursoNoEncontradoException("Lección no encontrada");
        if (l.Tipo != TipoLeccion.TEORIA)
            throw new InvalidOperationException("Las lecciones de ejercicio se completan resolviendo el reto");

        if (!await _repo.ExisteProgreso(usuarioId, leccionId))
            await _repo.GuardarProgreso(new ProgresoLeccion { UsuarioId = usuarioId, LeccionId = leccionId });
    }

    public async Task<List<PreguntaVista>> ObtenerExamenAsync(long moduloId, long usuarioId)
    {
        await ExigirExamenDesbloqueadoAsync(moduloId, usuarioId);
        var preguntas = await _repo.PreguntasPorModulo(moduloId);
        return preguntas.Select(p => new PreguntaVista(p.Id, p.Enunciado,
            new List<string> { p.OpcionA, p.OpcionB, p.OpcionC, p.OpcionD })).ToList();
    }

    public async Task<ResultadoExamen> CalificarExamenAsync(long moduloId, long usuarioId, Dictionary<long, int> respuestas)
    {
        await ExigirExamenDesbloqueadoAsync(moduloId, usuarioId);
        var preguntas = await _repo.PreguntasPorModulo(moduloId);
        if (preguntas.Count == 0)
            throw new RecursoNoEncontradoException("El módulo no tiene examen configurado");

        int aciertos = preguntas.Count(p => respuestas.TryGetValue(p.Id, out var elegida) && elegida == p.Correcta);
        int total = preguntas.Count;
        int porcentaje = aciertos * 100 / total;
        return new ResultadoExamen(aciertos, total, porcentaje, porcentaje >= UmbralAprobacion);
    }

    // ---- helpers ----

    private async Task ExigirExamenDesbloqueadoAsync(long moduloId, long usuarioId)
    {
        var d = await ObtenerModuloAsync(moduloId, usuarioId);
        if (!d.ExamenDesbloqueado)
            throw new ExamenBloqueadoException(
                $"Debes completar al menos {UmbralExamen}% de las lecciones para presentar el examen");
    }

    private async Task<bool> EstaCompletadaAsync(Leccion leccion, long? usuarioId)
    {
        if (usuarioId is null) return false;
        if (leccion.Tipo == TipoLeccion.EJERCICIO)
            return leccion.RetoId is not null && await _envios.TieneAceptadoAsync(usuarioId.Value, leccion.RetoId.Value);
        return await _repo.ExisteProgreso(usuarioId.Value, leccion.Id);
    }
}
