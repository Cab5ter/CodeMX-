package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import mx.codemx.modules.envios.Envio;
import mx.codemx.modules.envios.EnviosApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/envios")
@Tag(name = "Envios")
public class EnviosController {

    private final EnviosApi envios;

    public EnviosController(EnviosApi envios) {
        this.envios = envios;
    }

    @PostMapping
    public Envio enviar(@RequestBody Envio envio) {
        return envios.enviar(envio);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtener(@PathVariable long id) {
        return envios.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Envio> porUsuario(@PathVariable long usuarioId) {
        return envios.listarPorUsuario(usuarioId);
    }

    @GetMapping("/reto/{retoId}")
    public List<Envio> porReto(@PathVariable long retoId) {
        return envios.listarPorReto(retoId);
    }
}
