package mx.codemx.gateway;

import mx.codemx.ranking.api.RankingApi;
import mx.codemx.ranking.model.EntradaRanking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gateway interno → módulo Ranking.
 */
@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingApi ranking;

    public RankingController(RankingApi ranking) {
        this.ranking = ranking;
    }

    @GetMapping
    public ResponseEntity<List<EntradaRanking>> obtenerRanking() {
        return ResponseEntity.ok(ranking.obtenerRanking());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<EntradaRanking> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ranking.obtenerPorUsuario(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
