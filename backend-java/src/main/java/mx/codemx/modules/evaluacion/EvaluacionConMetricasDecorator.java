package mx.codemx.modules.evaluacion;

import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvaluacionConMetricasDecorator extends EvaluacionDecorator {

    private static final Logger log = LoggerFactory.getLogger(EvaluacionConMetricasDecorator.class);

    public EvaluacionConMetricasDecorator(EvaluacionStrategy envuelto) {
        super(envuelto);
    }

    @Override
    public ResultadoEvaluacion ejecutar(SolicitudEvaluacion solicitud, List<CasoPrueba> casos) {
        String estrategia = envuelto.getClass().getSimpleName();
        long inicio = System.nanoTime();

        try {
            ResultadoEvaluacion resultado = super.ejecutar(solicitud, casos);
            long transcurridoMs = milisegundosDesde(inicio);

            log.info("Evaluación del reto {} con {}: {} en {} ms",
                    solicitud.retoId(), estrategia, resultado.veredicto(), transcurridoMs);

            if (resultado.tiempoEjecucionMs() > 0) {
                return resultado;
            }
            return new ResultadoEvaluacion(
                    resultado.veredicto(), resultado.mensajeError(), transcurridoMs);

        } catch (RuntimeException e) {
            log.warn("Evaluación del reto {} con {} falló tras {} ms: {}",
                    solicitud.retoId(), estrategia, milisegundosDesde(inicio), e.getMessage());
            throw e;
        }
    }

    private static long milisegundosDesde(long inicioNanos) {
        return (System.nanoTime() - inicioNanos) / 1_000_000;
    }
}
