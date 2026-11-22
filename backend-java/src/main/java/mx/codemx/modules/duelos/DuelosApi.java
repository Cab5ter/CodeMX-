package mx.codemx.modules.duelos;

import java.util.List;
import mx.codemx.modules.evaluacion.ResultadoEvaluacion;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.Dificultad;

/**
 * API pública del módulo Duelos. La consumen el canal de tiempo real (para crear duelos,
 * evaluar soluciones y registrar al ganador) y el gateway (historial).
 */
public interface DuelosApi {

    /** Crea y persiste un duelo en curso entre dos jugadores. */
    Duelo crear(long jugador1Id, long jugador2Id, String titulo, Dificultad dificultad);

    /** Ejecuta el código del jugador contra los casos del problema (motor local Python). */
    ResultadoEvaluacion evaluar(String codigoFuente, List<CasoPrueba> casos);

    /**
     * Marca al ganador del duelo, lo cierra y aplica los puntos en el ranking
     * (+ al ganador, − al perdedor). Idempotente: si ya tenía ganador, no hace nada.
     */
    void registrarGanador(long dueloId, long ganadorId);

    List<Duelo> historial(long usuarioId);
}
