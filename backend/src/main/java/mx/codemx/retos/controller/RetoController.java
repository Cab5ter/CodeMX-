package mx.codemx.retos.controller;

import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Reto;
import mx.codemx.retos.service.RetoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retos")
public class RetoController {

    private final RetoService retoService;

    public RetoController(RetoService retoService) {
        this.retoService = retoService;
    }

    @GetMapping
    public ResponseEntity<List<Reto>> listar(
            @RequestParam(required = false) Dificultad dificultad) {
        if (dificultad != null) {
            return ResponseEntity.ok(retoService.listarPorDificultad(dificultad));
        }
        return ResponseEntity.ok(retoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reto> obtener(@PathVariable Long id) {
        return retoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
