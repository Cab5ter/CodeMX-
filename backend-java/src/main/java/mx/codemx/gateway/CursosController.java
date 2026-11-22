package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Cursos", description = """
        Ruta de aprendizaje: módulos, lecciones, seguimiento del progreso y examen de cada módulo.
        El examen se desbloquea al completar al menos el 70 % de las lecciones, y se aprueba con 70 %
        de aciertos.""")
public class CursosController {

    private final CursosApi cursos;

    public CursosController(CursosApi cursos) {
        this.cursos = cursos;
    }

    @Operation(
            summary = "Listar los módulos del curso",
            description = """
                    Devuelve el resumen de cada módulo: título, ícono y número de lecciones. Si se envía
                    `usuarioId`, cada resumen incluye además cuántas lecciones lleva completadas ese
                    estudiante, su porcentaje de avance y si ya tiene desbloqueado el examen. Sin
                    `usuarioId` el progreso se devuelve en cero.""")
    @ApiResponse(responseCode = "200", description = "Lista de módulos")
    @GetMapping
    public ResponseEntity<List<ModuloResumen>> listar(
            @Parameter(description = "Estudiante del cual calcular el progreso. Opcional.")
            @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.listarModulos(usuarioId));
    }

    @Operation(
            summary = "Obtener un módulo con sus lecciones",
            description = """
                    Devuelve el módulo completo con la lista ordenada de sus lecciones, marcando cuáles
                    ya completó el estudiante indicado. Incluye `umbralExamen`, el porcentaje de avance
                    necesario para desbloquear el examen.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Módulo encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un módulo con ese identificador")
    })
    @GetMapping("/modulos/{moduloId}")
    public ResponseEntity<ModuloDetalle> obtenerModulo(
            @Parameter(description = "Identificador del módulo") @PathVariable long moduloId,
            @Parameter(description = "Estudiante del cual calcular el progreso. Opcional.")
            @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerModulo(moduloId, usuarioId));
    }

    @Operation(
            summary = "Obtener el contenido de una lección",
            description = """
                    Devuelve el contenido de la lección y su ejemplo de código. Las lecciones de tipo
                    `EJERCICIO` traen además el `retoId` que hay que resolver para completarlas.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lección encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una lección con ese identificador")
    })
    @GetMapping("/lecciones/{leccionId}")
    public ResponseEntity<LeccionDetalle> obtenerLeccion(
            @Parameter(description = "Identificador de la lección") @PathVariable long leccionId,
            @Parameter(description = "Estudiante del cual verificar si ya la completó. Opcional.")
            @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerLeccion(leccionId, usuarioId));
    }

    @Operation(
            summary = "Marcar una lección de teoría como completada",
            description = """
                    Registra el avance del estudiante en una lección. Es **idempotente**: repetir la
                    llamada no duplica el progreso.

                    Solo aplica a lecciones de tipo `TEORIA`. Las de tipo `EJERCICIO` no se completan
                    por aquí, sino resolviendo el reto asociado con un envío `ACEPTADO`; intentarlo
                    devuelve `400`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Progreso registrado"),
            @ApiResponse(responseCode = "400", description = "La lección es de tipo EJERCICIO y se completa resolviendo su reto"),
            @ApiResponse(responseCode = "404", description = "No existe una lección con ese identificador")
    })
    @PostMapping("/lecciones/{leccionId}/completar")
    public ResponseEntity<Void> completar(
            @Parameter(description = "Identificador de la lección") @PathVariable long leccionId,
            @Parameter(description = "Estudiante que completó la lección") @RequestParam long usuarioId) {
        cursos.completarLeccion(leccionId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener las preguntas del examen de un módulo",
            description = """
                    Devuelve las preguntas ordenadas, cada una con sus cuatro opciones. **La respuesta
                    correcta nunca se envía al cliente**: la calificación ocurre en el servidor.

                    El examen está bloqueado hasta que el estudiante complete al menos el 70 % de las
                    lecciones del módulo; si no llega a ese avance, responde `403`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preguntas del examen, sin las respuestas correctas"),
            @ApiResponse(responseCode = "403", description = "El estudiante aún no alcanza el 70 % de avance en el módulo"),
            @ApiResponse(responseCode = "404", description = "No existe un módulo con ese identificador")
    })
    @GetMapping("/modulos/{moduloId}/examen")
    public ResponseEntity<List<PreguntaVista>> examen(
            @Parameter(description = "Identificador del módulo") @PathVariable long moduloId,
            @Parameter(description = "Estudiante que presenta el examen") @RequestParam long usuarioId) {
        return ResponseEntity.ok(cursos.obtenerExamen(moduloId, usuarioId));
    }

    @Operation(
            summary = "Calificar el examen de un módulo",
            description = """
                    Recibe las respuestas del estudiante como un mapa `idPregunta: índiceDeOpción`
                    (0 = A, 1 = B, 2 = C, 3 = D) y las compara contra las correctas en el servidor.

                    Devuelve aciertos, total de preguntas, porcentaje y si aprobó. El umbral de
                    aprobación es 70 %. Las preguntas que no aparezcan en el mapa cuentan como
                    incorrectas.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Examen calificado"),
            @ApiResponse(responseCode = "403", description = "El estudiante aún no alcanza el 70 % de avance en el módulo"),
            @ApiResponse(responseCode = "404", description = "El módulo no existe o no tiene examen configurado")
    })
    @PostMapping("/modulos/{moduloId}/examen")
    public ResponseEntity<ResultadoExamen> calificar(
            @Parameter(description = "Identificador del módulo") @PathVariable long moduloId,
            @Parameter(description = "Estudiante que presenta el examen") @RequestParam long usuarioId,
            @RequestBody Map<Long, Integer> respuestas) {
        return ResponseEntity.ok(cursos.calificarExamen(moduloId, usuarioId, respuestas));
    }
}
