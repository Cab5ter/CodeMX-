package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RestController
@RequestMapping("/api/ranking")
@Tag(name = "Ranking", description = """
        Tabla de posiciones. Las entradas se actualizan solas cuando un estudiante resuelve un reto
        por primera vez; no hay endpoints de escritura.""")
public class RankingController {

    private final RankingApi ranking;

    public RankingController(RankingApi ranking) {
        this.ranking = ranking;
    }

    @Operation(
            summary = "Obtener la tabla de posiciones",
            description = """
                    Devuelve todas las entradas ordenadas de mayor a menor puntaje, ya resueltas con el
                    nombre del estudiante para pintarlas directo en la interfaz. Si un usuario fue
                    eliminado o no tiene nombre, aparece como `Usuario #<id>`.

                    El puntaje se acumula por dificultad del reto: BASICO 10, INTERMEDIO 25 y
                    AVANZADO 50, y solo cuenta la primera vez que se resuelve cada reto.""")
    @ApiResponse(responseCode = "200", description = "Tabla de posiciones ordenada por puntaje descendente")
    @GetMapping
    public ResponseEntity<List<EntradaRankingDto>> obtener() {
        return ResponseEntity.ok(ranking.obtenerRankingConNombres());
    }

    @Operation(
            summary = "Obtener la entrada de ranking de un usuario",
            description = """
                    Devuelve el puntaje acumulado y el número de retos resueltos de un solo estudiante.
                    Responde `404` mientras el usuario no haya resuelto ningún reto, porque hasta
                    entonces no existe una entrada suya en la tabla.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada encontrada"),
            @ApiResponse(responseCode = "404", description = "El usuario todavía no tiene entrada en el ranking")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<EntradaRanking> porUsuario(
            @Parameter(description = "Identificador del usuario") @PathVariable long usuarioId) {
        return ranking.obtenerPorUsuario(usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
