package mx.codemx.ranking.service;

import mx.codemx.ranking.api.RankingApi;
import mx.codemx.ranking.model.EntradaRanking;
import mx.codemx.ranking.repository.RankingRepository;
import mx.codemx.retos.api.RetosApi;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RankingService implements RankingApi {

    private final RankingRepository rankingRepository;
    private final RetosApi retosApi;   // Ranking consulta los puntos del reto por interfaz

    public RankingService(RankingRepository rankingRepository, RetosApi retosApi) {
        this.rankingRepository = rankingRepository;
        this.retosApi = retosApi;
    }

    @Override
    public List<EntradaRanking> obtenerRanking() {
        return rankingRepository.findAllByOrderByPuntajeTotalDesc();
    }

    @Override
    public Optional<EntradaRanking> obtenerPorUsuario(Long usuarioId) {
        return rankingRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public void registrarAcierto(Long usuarioId, Long retoId) {
        int puntos = retosApi.puntosPorReto(retoId);

        EntradaRanking entrada = rankingRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    EntradaRanking nueva = new EntradaRanking();
                    nueva.setUsuarioId(usuarioId);
                    return nueva;
                });

        entrada.setPuntajeTotal(entrada.getPuntajeTotal() + puntos);
        entrada.setRetosResueltos(entrada.getRetosResueltos() + 1);
        entrada.setActualizadoEn(LocalDateTime.now());
        rankingRepository.save(entrada);
    }
}
