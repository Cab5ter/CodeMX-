namespace CodeMX.Api.Modules.Retos;

/// <summary>Caso de prueba de un reto (entrada y salida esperada).</summary>
public class CasoPrueba
{
    public long Id { get; set; }
    public long RetoId { get; set; }
    public string? InputData { get; set; }
    public string OutputEsperado { get; set; } = "";
}
