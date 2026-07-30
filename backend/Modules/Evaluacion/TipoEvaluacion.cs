namespace CodeMX.Api.Modules.Evaluacion;

/// <summary>Estrategias de evaluación disponibles (entrada del Factory Method).</summary>
public enum TipoEvaluacion
{
    Remota,  // ejecuta el código en el Servicio Python externo (HTTP)
    Local    // ejecuta el código localmente con un proceso python3 (respaldo)
}
