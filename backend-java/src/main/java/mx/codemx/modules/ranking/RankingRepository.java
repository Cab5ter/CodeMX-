package mx.codemx.modules.ranking;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos del módulo Ranking. */
public interface RankingRepository extends JpaRepository<EntradaRanking, Long> {

    List<EntradaRanking> findAllByOrderByPuntajeTotalDesc();

    Optional<EntradaRanking> findByUsuarioId(long usuarioId);
}
