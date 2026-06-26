package mx.codemx.ranking.api;

import mx.codemx.ranking.model.EntradaRanking;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Ranking.
 *
 * El módulo Envíos la consume para registrar un acierto cuando un envío es ACEPTADO.
 * Internamente, Ranking consulta los puntos del reto al módulo Retos (vía RetosApi)
 * y actualiza la tabla de posiciones. El gateway la consume para mostrar el ranking.
 */
public interface RankingApi {

    List<EntradaRanking> obtenerRanking();

    Optional<EntradaRanking> obtenerPorUsuario(Long usuarioId);

    /**
     * Registra que un usuario resolvió un reto por primera vez.
     * Ranking calcula los puntos consultando al módulo Retos.
     */
    void registrarAcierto(Long usuarioId, Long retoId);
}
