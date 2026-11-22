namespace CodeMX.Api.Modules.Duelos;

/// <summary>Estados por los que pasa un duelo 1 vs 1.</summary>
public enum EstadoDuelo
{
    EnCurso,    // ambos jugadores recibieron el problema y compiten
    Terminado   // alguien resolvió primero (o hubo abandono)
}
