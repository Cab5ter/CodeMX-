package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.Dificultad;
import mx.codemx.modules.retos.RetosApi;
import mx.codemx.modules.retos.domain.RetoDominio;
import mx.codemx.modules.retos.mapper.RetoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/retos")
@Tag(name = "Retos")
public class RetosController {

    private final RetosApi retos;
    private final RetoMapper mapper;

    public RetosController(RetosApi retos, RetoMapper mapper) {
        this.retos = retos;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<RetoDominio>> listar(@RequestParam(required = false) Dificultad dificultad) {
        return ResponseEntity.ok(mapper.toDominio(
                dificultad != null ? retos.listarPorDificultad(dificultad) : retos.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetoDominio> obtener(@PathVariable long id) {
        return retos.buscarPorId(id)
                .map(mapper::toDominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/ejemplo")
    public ResponseEntity<CasoPrueba> ejemplo(@PathVariable long id) {
        return retos.obtenerEjemplo(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RetoDominio> crear(@RequestBody RetoDominio reto) {
        RetoDominio creado = mapper.toDominio(retos.guardar(mapper.toEntity(reto)));
        return ResponseEntity.created(URI.create("/api/retos/" + creado.getId())).body(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        if (retos.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        retos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
