package mx.codemx.evaluacion.api;

import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;

/**
 * API pública del módulo Evaluación.
 *
 * El módulo Envíos la consume para evaluar el código de un envío. Internamente,
 * Evaluación envía el código al Servicio Python (sandbox aislado) y devuelve el
 * resultado de las pruebas. El cliente del sandbox queda oculto tras esta interfaz.
 */
public interface EvaluacionApi {

    ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud);
}
