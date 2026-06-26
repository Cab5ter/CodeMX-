package mx.codemx.envios.controller;

import mx.codemx.envios.model.Envio;
import mx.codemx.envios.service.EnvioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    public ResponseEntity<Envio> enviar(@RequestBody Envio envio) {
        return ResponseEntity.ok(envioService.guardar(envio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtener(@PathVariable Long id) {
        return envioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Envio>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(envioService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/reto/{retoId}")
    public ResponseEntity<List<Envio>> porReto(@PathVariable Long retoId) {
        return ResponseEntity.ok(envioService.listarPorReto(retoId));
    }
}
