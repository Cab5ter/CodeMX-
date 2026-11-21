package mx.codemx.modules.ranking;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Ranking. La consume Envíos para registrar un acierto.
 * Internamente consulta los puntos del reto al módulo Retos (vía RetosApi).
 */
public interface RankingApi {

    List<EntradaRanking> obtenerRanking();

    /** Ranking con el nombre de cada jugador (para mostrar en la interfaz). */
    List<EntradaRankingDto> obtenerRankingConNombres();

    Optional<EntradaRanking> obtenerPorUsuario(long usuarioId);

    /** Registra que un usuario resolvió un reto por primera vez. */
    void registrarAcierto(long usuarioId, long retoId);

    /**
     * Aplica el resultado de un duelo 1 vs 1: suma puntos al ganador y resta al perdedor
     * (el puntaje del perdedor nunca baja de 0).
     */
    void registrarResultadoDuelo(long ganadorId, long perdedorId, int puntosGanador, int puntosPerdedor);
}
