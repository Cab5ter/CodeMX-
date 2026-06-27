namespace CodeMX.Api.Modules.Cursos;

/// <summary>Se lanza cuando el alumno intenta el examen sin el avance mínimo. Mapea a HTTP 403.</summary>
public class ExamenBloqueadoException : Exception
{
    public ExamenBloqueadoException(string message) : base(message) { }
}

/// <summary>Recurso de cursos no encontrado. Mapea a HTTP 404.</summary>
public class RecursoNoEncontradoException : Exception
{
    public RecursoNoEncontradoException(string message) : base(message) { }
}
