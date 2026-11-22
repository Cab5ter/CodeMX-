package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import mx.codemx.modules.cursos.CursosApi;
import mx.codemx.modules.cursos.CursosDtos.LeccionDetalle;
import mx.codemx.modules.cursos.CursosDtos.ModuloDetalle;
import mx.codemx.modules.cursos.CursosDtos.ModuloResumen;
import mx.codemx.modules.cursos.CursosDtos.PreguntaVista;
import mx.codemx.modules.cursos.CursosDtos.ResultadoExamen;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos")
public class CursosController {

    private final CursosApi cursos;

    public CursosController(CursosApi cursos) {
        this.cursos = cursos;
    }

    @GetMapping
    public ResponseEntity<List<ModuloResumen>> listar(@RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.listarModulos(usuarioId));
    }

    @GetMapping("/modulos/{moduloId}")
    public ResponseEntity<ModuloDetalle> obtenerModulo(@PathVariable long moduloId,
                                                       @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerModulo(moduloId, usuarioId));
    }

    @GetMapping("/lecciones/{leccionId}")
    public ResponseEntity<LeccionDetalle> obtenerLeccion(@PathVariable long leccionId,
                                                         @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerLeccion(leccionId, usuarioId));
    }

    @PostMapping("/lecciones/{leccionId}/completar")
    public ResponseEntity<Void> completar(@PathVariable long leccionId, @RequestParam long usuarioId) {
        cursos.completarLeccion(leccionId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/modulos/{moduloId}/examen")
    public ResponseEntity<List<PreguntaVista>> examen(@PathVariable long moduloId, @RequestParam long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerExamen(moduloId, usuarioId));
    }

    @PostMapping("/modulos/{moduloId}/examen")
    public ResponseEntity<ResultadoExamen> calificar(@PathVariable long moduloId, @RequestParam long usuarioId,
                                                     @RequestBody Map<Long, Integer> respuestas) {
        return ResponseEntity.ok(cursos.calificarExamen(moduloId, usuarioId, respuestas));
    }
}
