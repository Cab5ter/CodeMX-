package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import mx.codemx.modules.duelos.Duelo;
import mx.codemx.modules.duelos.DuelosApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/duelos")
@Tag(name = "Duelos", description = """
        Historial de duelos 1 contra 1. El emparejamiento y la partida en vivo **no ocurren por REST**:
        viajan por WebSocket en `/api/hub/duelos`, así que aquí solo se consultan los duelos ya
        terminados.""")
public class DuelosController {

    private final DuelosApi duelos;

    public DuelosController(DuelosApi duelos) {
        this.duelos = duelos;
    }

    @Operation(
            summary = "Listar el historial de duelos de un usuario",
            description = """
                    Devuelve los duelos en los que participó el estudiante, ya sea como jugador 1 o
                    como jugador 2, ordenados del más reciente al más antiguo. Cada duelo trae el título
                    del problema que se jugó, su dificultad, el estado (`EN_CURSO` o `TERMINADO`) y el
                    identificador del ganador. El problema lo genera la API de Claude al momento de
                    emparejar; si no está disponible, se usa un reto sembrado de respaldo.""")
    @ApiResponse(responseCode = "200", description = "Historial de duelos (posiblemente vacío)")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Duelo>> historial(
            @Parameter(description = "Identificador del usuario") @PathVariable long usuarioId) {
        return ResponseEntity.ok(duelos.historial(usuarioId));
    }
}
