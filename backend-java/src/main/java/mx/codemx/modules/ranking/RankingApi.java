package mx.codemx.modules.ranking;

import java.util.List;
import java.util.Optional;

public interface RankingApi {

    List<EntradaRanking> obtenerRanking();

    List<EntradaRankingDto> obtenerRankingConNombres();

    Optional<EntradaRanking> obtenerPorUsuario(long usuarioId);

    void registrarAcierto(long usuarioId, long retoId);

    void registrarResultadoDuelo(long ganadorId, long perdedorId, int puntosGanador, int puntosPerdedor);
}
