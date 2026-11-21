package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import mx.codemx.modules.ranking.EntradaRanking;
import mx.codemx.modules.ranking.EntradaRankingDto;
import mx.codemx.modules.ranking.RankingApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Gateway / API REST → módulo Ranking. */
@RestController
@RequestMapping("/api/ranking")
@Tag(name = "Ranking")
public class RankingController {

    private final RankingApi ranking;

    public RankingController(RankingApi ranking) {
        this.ranking = ranking;
    }

    @GetMapping
    public List<EntradaRankingDto> obtener() {
        return ranking.obtenerRankingConNombres();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<EntradaRanking> porUsuario(@PathVariable long usuarioId) {
        return ranking.obtenerPorUsuario(usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
