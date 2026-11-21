package mx.codemx.modules.evaluacion;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.RetosApi;
import org.springframework.stereotype.Service;

/**
 * Contexto del patrón Strategy. Obtiene los casos de prueba del reto (vía RetosApi) y
 * delega la ejecución a una estrategia creada por el Factory Method: intenta primero la
 * estrategia remota (Servicio Python) y, si falla, recurre a la estrategia local.
 */
@Service
public class EvaluacionService implements EvaluacionApi {

    private final EvaluadorStrategyFactory factory;
    private final RetosApi retos;

    public EvaluacionService(EvaluadorStrategyFactory factory, RetosApi retos) {
        this.factory = factory;
        this.retos = retos;
    }

    @Override
    public ResultadoEvaluacion evaluar(SolicitudEvaluacion solicitud) {
        List<CasoPrueba> casos = retos.obtenerCasos(solicitud.retoId());

        try {
            // Estrategia preferida: ejecución remota en el Servicio Python.
            return factory.crear(TipoEvaluacion.REMOTA).ejecutar(solicitud, casos);
        } catch (Exception e) {
            // Respaldo: ejecución local mientras el Servicio Python no esté disponible.
            return factory.crear(TipoEvaluacion.LOCAL).ejecutar(solicitud, casos);
        }
    }
}
