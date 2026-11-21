package mx.codemx.modules.evaluacion;

/**
 * API pública del módulo Evaluación. La consume Envíos. Internamente envía el código
 * al Servicio Python (sandbox aislado) por HTTP y devuelve el resultado de las pruebas.
 */
public interface EvaluacionApi {

    ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud);
}
