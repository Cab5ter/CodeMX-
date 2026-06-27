namespace CodeMX.Api.Modules.Cursos;

/// <summary>
/// Marca que un usuario completó una lección de TEORIA. Las de EJERCICIO no usan
/// esta tabla: su avance se deduce de los envíos ACEPTADOS (módulo Envíos).
/// </summary>
public class ProgresoLeccion
{
    public long Id { get; set; }
    public long UsuarioId { get; set; }
    public long LeccionId { get; set; }
    public DateTime CompletadaEn { get; set; } = DateTime.UtcNow;
}
