package mx.codemx.cursos.controller;

import mx.codemx.cursos.dto.*;
import mx.codemx.cursos.service.CursoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    /** Menú principal: todos los módulos con el avance del usuario. */
    @GetMapping
    public ResponseEntity<List<ModuloResumen>> listar(@RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursoService.listarModulos(usuarioId));
    }

    /** Detalle de un módulo: lecciones, progreso y estado del examen. */
    @GetMapping("/modulos/{moduloId}")
    public ResponseEntity<ModuloDetalle> obtenerModulo(@PathVariable Long moduloId,
                                                       @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursoService.obtenerModulo(moduloId, usuarioId));
    }

    /** Contenido de una lección de teoría. */
    @GetMapping("/lecciones/{leccionId}")
    public ResponseEntity<LeccionDetalle> obtenerLeccion(@PathVariable Long leccionId,
                                                         @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursoService.obtenerLeccion(leccionId, usuarioId));
    }

    /** Marca una lección de teoría como completada. */
    @PostMapping("/lecciones/{leccionId}/completar")
    public ResponseEntity<Void> completar(@PathVariable Long leccionId, @RequestParam Long usuarioId) {
        cursoService.completarLeccion(leccionId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /** Obtiene las preguntas del examen (sólo si está desbloqueado). */
    @GetMapping("/modulos/{moduloId}/examen")
    public ResponseEntity<List<PreguntaVista>> examen(@PathVariable Long moduloId, @RequestParam Long usuarioId) {
        return ResponseEntity.ok(cursoService.obtenerExamen(moduloId, usuarioId));
    }

    /** Envía las respuestas del examen y devuelve la calificación. */
    @PostMapping("/modulos/{moduloId}/examen")
    public ResponseEntity<ResultadoExamen> calificar(@PathVariable Long moduloId,
                                                     @RequestParam Long usuarioId,
                                                     @RequestBody Map<Long, Integer> respuestas) {
        return ResponseEntity.ok(cursoService.calificarExamen(moduloId, usuarioId, respuestas));
    }
}
