package mx.codemx.gateway;

import mx.codemx.cursos.api.CursosApi;
import mx.codemx.cursos.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Gateway interno → módulo Cursos (plataforma de aprendizaje).
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursosApi cursos;

    public CursoController(CursosApi cursos) {
        this.cursos = cursos;
    }

    @GetMapping
    public ResponseEntity<List<ModuloResumen>> listar(@RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.listarModulos(usuarioId));
    }

    @GetMapping("/modulos/{moduloId}")
    public ResponseEntity<ModuloDetalle> obtenerModulo(@PathVariable Long moduloId,
                                                       @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerModulo(moduloId, usuarioId));
    }

    @GetMapping("/lecciones/{leccionId}")
    public ResponseEntity<LeccionDetalle> obtenerLeccion(@PathVariable Long leccionId,
                                                         @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerLeccion(leccionId, usuarioId));
    }

    @PostMapping("/lecciones/{leccionId}/completar")
    public ResponseEntity<Void> completar(@PathVariable Long leccionId, @RequestParam Long usuarioId) {
        cursos.completarLeccion(leccionId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/modulos/{moduloId}/examen")
    public ResponseEntity<List<PreguntaVista>> examen(@PathVariable Long moduloId, @RequestParam Long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerExamen(moduloId, usuarioId));
    }

    @PostMapping("/modulos/{moduloId}/examen")
    public ResponseEntity<ResultadoExamen> calificar(@PathVariable Long moduloId,
                                                     @RequestParam Long usuarioId,
                                                     @RequestBody Map<Long, Integer> respuestas) {
        return ResponseEntity.ok(cursos.calificarExamen(moduloId, usuarioId, respuestas));
    }
}
