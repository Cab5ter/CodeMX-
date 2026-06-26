package mx.codemx.gateway;

import mx.codemx.retos.api.RetosApi;
import mx.codemx.retos.model.CasoPrueba;
import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Reto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gateway interno → módulo Retos.
 */
@RestController
@RequestMapping("/api/retos")
public class RetoController {

    private final RetosApi retos;

    public RetoController(RetosApi retos) {
        this.retos = retos;
    }

    @GetMapping
    public ResponseEntity<List<Reto>> listar(@RequestParam(required = false) Dificultad dificultad) {
        if (dificultad != null) return ResponseEntity.ok(retos.listarPorDificultad(dificultad));
        return ResponseEntity.ok(retos.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reto> obtener(@PathVariable Long id) {
        return retos.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/ejemplo")
    public ResponseEntity<CasoPrueba> obtenerEjemplo(@PathVariable Long id) {
        return retos.obtenerEjemplo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reto> crear(@RequestBody Reto reto) {
        return ResponseEntity.ok(retos.guardar(reto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        retos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
