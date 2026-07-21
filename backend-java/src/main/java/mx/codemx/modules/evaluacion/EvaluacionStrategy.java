package mx.codemx.modules.evaluacion;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;

/**
 * Patrón <b>Strategy</b> (GoF). Define el algoritmo intercambiable para ejecutar el código
 * de un envío contra sus casos de prueba. Cada estrategia concreta resuelve el "cómo"
 * (remoto vía Servicio Python, o local vía proceso) sin que el resto del sistema cambie.
 */
public interface EvaluacionStrategy {

    ResultadoEvaluacion ejecutar(SolicitudEvaluacion solicitud, List<CasoPrueba> casos);
}
