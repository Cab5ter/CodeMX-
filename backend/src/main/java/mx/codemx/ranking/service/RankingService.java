package mx.codemx.ranking.service;

import mx.codemx.ranking.model.EntradaRanking;
import mx.codemx.ranking.repository.RankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RankingService {

    private final RankingRepository rankingRepository;

    public RankingService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    public List<EntradaRanking> obtenerRanking() {
        return rankingRepository.findAllByOrderByPuntajeTotalDesc();
    }

    public Optional<EntradaRanking> obtenerPorUsuario(Long usuarioId) {
        return rankingRepository.findByUsuarioId(usuarioId);
    }

    public EntradaRanking guardar(EntradaRanking entrada) {
        return rankingRepository.save(entrada);
    }
}
