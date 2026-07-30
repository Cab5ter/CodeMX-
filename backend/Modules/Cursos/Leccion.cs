namespace CodeMX.Api.Modules.Cursos;

/// <summary>Lección de un módulo: teoría o ejercicio enlazado a un reto.</summary>
public class Leccion
{
    public long Id { get; set; }
    public long ModuloId { get; set; }
    public string Titulo { get; set; } = "";
    public TipoLeccion Tipo { get; set; }

    // Para TEORIA: el texto explicativo y un ejemplo de código Python (opcional).
    public string? Contenido { get; set; }
    public string? EjemploCodigo { get; set; }

    // Para EJERCICIO: el reto que el alumno debe resolver.
    public long? RetoId { get; set; }

    public int Orden { get; set; }
}
