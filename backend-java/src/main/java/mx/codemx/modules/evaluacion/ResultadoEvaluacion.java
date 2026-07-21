package mx.codemx.modules.evaluacion;

/** Resultado que el Servicio Python devuelve hacia Envíos. */
public record ResultadoEvaluacion(String veredicto, String mensajeError, long tiempoEjecucionMs) {
}
