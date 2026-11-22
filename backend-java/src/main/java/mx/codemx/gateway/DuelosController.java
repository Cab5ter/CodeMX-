package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import mx.codemx.modules.duelos.Duelo;
import mx.codemx.modules.duelos.DuelosApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gateway / API REST → módulo Duelos. El duelo en vivo ocurre por WebSocket
 * (/api/hub/duelos); este controlador sólo expone el historial para la interfaz.
 */
@RestController
@RequestMapping("/api/duelos")
@Tag(name = "Duelos")
public class DuelosController {

    private final DuelosApi duelos;

    public DuelosController(DuelosApi duelos) {
        this.duelos = duelos;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Duelo> historial(@PathVariable long usuarioId) {
        return duelos.historial(usuarioId);
    }
}
