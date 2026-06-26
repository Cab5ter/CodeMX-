package mx.codemx.ranking.controller;

import mx.codemx.ranking.model.EntradaRanking;
import mx.codemx.ranking.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public ResponseEntity<List<EntradaRanking>> obtenerRanking() {
        return ResponseEntity.ok(rankingService.obtenerRanking());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<EntradaRanking> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return rankingService.obtenerPorUsuario(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
