package mx.codemx.retos.controller;

import mx.codemx.retos.model.CasoPrueba;
import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Envio;
import mx.codemx.retos.model.EnvioRequest;
import mx.codemx.retos.model.Reto;
import mx.codemx.retos.service.EnvioService;
import mx.codemx.retos.service.RetoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retos")
public class RetoController {

    private final RetoService retoService;
    private final EnvioService envioService;

    public RetoController(RetoService retoService, EnvioService envioService) {
        this.retoService = retoService;
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<List<Reto>> listar(@RequestParam(required = false) Dificultad dificultad) {
        if (dificultad != null) return ResponseEntity.ok(retoService.listarPorDificultad(dificultad));
        return ResponseEntity.ok(retoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reto> obtener(@PathVariable Long id) {
        return retoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/ejemplo")
    public ResponseEntity<CasoPrueba> obtenerEjemplo(@PathVariable Long id) {
        return retoService.obtenerEjemplo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Punto de entrada del flujo de la Vista de Procesos (ADR-02):
     * el estudiante envía su solución a un reto. El módulo Retos guarda el envío,
     * lo manda al Evaluador y devuelve el veredicto (AC / WA / TLE).
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<Envio> submit(@PathVariable Long id, @RequestBody EnvioRequest req) {
        Envio envio = new Envio();
        envio.setRetoId(id);
        envio.setUsuarioId(req.usuarioId());
        envio.setCodigoFuente(req.codigoFuente());
        return ResponseEntity.ok(envioService.enviar(envio));
    }

    @GetMapping("/{id}/envios")
    public ResponseEntity<List<Envio>> enviosPorReto(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.listarPorReto(id));
    }

    @GetMapping("/envios/usuario/{usuarioId}")
    public ResponseEntity<List<Envio>> enviosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(envioService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<Reto> crear(@RequestBody Reto reto) {
        return ResponseEntity.ok(retoService.guardar(reto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        retoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
