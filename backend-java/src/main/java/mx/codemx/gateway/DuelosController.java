package mx.codemx.gateway;

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
@Tag(name = "Duelos")
public class DuelosController {

    private final DuelosApi duelos;

    public DuelosController(DuelosApi duelos) {
        this.duelos = duelos;
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Duelo>> historial(@PathVariable long usuarioId) {
        return ResponseEntity.ok(duelos.historial(usuarioId));
    }
}
