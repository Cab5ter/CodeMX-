package mx.codemx.gateway;

import mx.codemx.envios.api.EnviosApi;
import mx.codemx.envios.model.Envio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gateway interno → módulo Envíos.
 * Es el punto donde el frontend manda las soluciones del estudiante.
 */
@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnviosApi envios;

    public EnvioController(EnviosApi envios) {
        this.envios = envios;
    }

    @PostMapping
    public ResponseEntity<Envio> enviar(@RequestBody Envio envio) {
        return ResponseEntity.ok(envios.enviar(envio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtener(@PathVariable Long id) {
        return envios.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Envio>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(envios.listarPorUsuario(usuarioId));
    }

    @GetMapping("/reto/{retoId}")
    public ResponseEntity<List<Envio>> porReto(@PathVariable Long retoId) {
        return ResponseEntity.ok(envios.listarPorReto(retoId));
    }
}
