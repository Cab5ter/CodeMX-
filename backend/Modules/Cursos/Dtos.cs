namespace CodeMX.Api.Modules.Cursos;

/// <summary>Tarjeta de un módulo en el menú principal, con el avance del usuario.</summary>
public record ModuloResumen(
    long Id, string Titulo, string Descripcion, string Icono,
    int TotalLecciones, int LeccionesCompletadas, int Progreso, bool ExamenDesbloqueado);

/// <summary>Lección dentro del detalle de un módulo, con su estado de avance.</summary>
public record LeccionVista(
    long Id, string Titulo, TipoLeccion Tipo, long? RetoId, bool Completada, int Orden);

/// <summary>Vista completa de un módulo: sus lecciones, avance y estado del examen.</summary>
public record ModuloDetalle(
    long Id, string Titulo, string Descripcion, string Icono,
    List<LeccionVista> Lecciones, int TotalLecciones, int LeccionesCompletadas,
    int Progreso, int UmbralExamen, bool ExamenDesbloqueado);

/// <summary>Contenido completo de una lección de teoría.</summary>
public record LeccionDetalle(
    long Id, long ModuloId, string TituloModulo, string Titulo, TipoLeccion Tipo,
    string? Contenido, string? EjemploCodigo, long? RetoId, bool Completada);

/// <summary>Pregunta del examen como la ve el alumno (sin la respuesta correcta).</summary>
public record PreguntaVista(long Id, string Enunciado, List<string> Opciones);

/// <summary>Resultado de calificar un examen.</summary>
public record ResultadoExamen(int Aciertos, int Total, int Porcentaje, bool Aprobado);
