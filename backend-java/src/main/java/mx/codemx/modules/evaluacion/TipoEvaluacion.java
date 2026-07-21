package mx.codemx.modules.evaluacion;

/** Estrategias de evaluación disponibles (entrada del Factory Method). */
public enum TipoEvaluacion {
    /** Ejecuta el código en el Servicio Python externo (HTTP). */
    REMOTA,
    /** Ejecuta el código localmente con un proceso python3 (respaldo). */
    LOCAL
}
