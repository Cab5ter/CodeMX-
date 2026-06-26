package mx.codemx.ranking.repository;

import mx.codemx.ranking.model.EntradaRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<EntradaRanking, Long> {
    Optional<EntradaRanking> findByUsuarioId(Long usuarioId);
    List<EntradaRanking> findAllByOrderByPuntajeTotalDesc();
}
