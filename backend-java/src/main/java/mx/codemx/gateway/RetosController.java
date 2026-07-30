package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import mx.codemx.modules.cursos.CursosApi;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.Dificultad;
import mx.codemx.modules.retos.RetosApi;
import mx.codemx.modules.retos.domain.RetoDominio;
import mx.codemx.modules.retos.mapper.RetoMapper;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Retos", description = "Catálogo de retos de programación, su enunciado y su caso de prueba de ejemplo.")
public class RetosController {

    private final RetosApi retos;
    private final CursosApi cursos;
    private final RetoMapper mapper;

    public RetosController(RetosApi retos, CursosApi cursos, RetoMapper mapper) {
        this.retos = retos;
        this.cursos = cursos;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Listar el catálogo de retos",
            description = """
                    Devuelve todos los retos disponibles. Con el parámetro `dificultad` se filtra por
                    nivel. La dificultad también determina cuántos puntos otorga el reto al resolverse
                    por primera vez: BASICO 10, INTERMEDIO 25 y AVANZADO 50.""")
    @ApiResponse(responseCode = "200", description = "Lista de retos (posiblemente vacía)")
    @GetMapping
    public ResponseEntity<List<RetoDominio>> listar(
            @Parameter(description = "Nivel por el cual filtrar. Si se omite, devuelve el catálogo completo.")
            @RequestParam(required = false) Dificultad dificultad) {
        return ResponseEntity.ok(mapper.toDominio(
                dificultad != null ? retos.listarPorDificultad(dificultad) : retos.listarTodos()));
    }

    @Operation(
            summary = "Obtener un reto por su identificador",
            description = """
                    Devuelve el título, la descripción y la dificultad del reto. No incluye los casos de
                    prueba: esos se resuelven en el servidor al evaluar un envío para que el estudiante
                    no pueda leerlos. Para mostrar una muestra de entrada y salida, usar
                    `GET /api/retos/{id}/ejemplo`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reto encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un reto con ese identificador")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RetoDominio> obtener(
            @Parameter(description = "Identificador del reto") @PathVariable long id) {
        return retos.buscarPorId(id)
                .map(mapper::toDominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Obtener el caso de prueba de ejemplo de un reto",
            description = """
                    Devuelve el único caso público del reto, con su entrada y su salida esperada, para
                    mostrarlo en el enunciado como muestra. Los demás casos permanecen ocultos y solo
                    se usan al calificar un envío.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Caso de ejemplo del reto"),
            @ApiResponse(responseCode = "404", description = "El reto no existe o no tiene casos configurados")
    })
    @GetMapping("/{id}/ejemplo")
    public ResponseEntity<CasoPrueba> ejemplo(
            @Parameter(description = "Identificador del reto") @PathVariable long id) {
        return retos.obtenerEjemplo(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Crear un reto",
            description = """
                    Da de alta un reto nuevo en el catálogo y devuelve la cabecera `Location` con su
                    ubicación. Los casos de prueba se cargan por separado. El `id` lo asigna siempre la
                    base de datos: si el cuerpo trae uno, se ignora.""")
    @ApiResponse(responseCode = "201", description = "Reto creado")
    @PostMapping
    public ResponseEntity<RetoDominio> crear(@Valid @RequestBody RetoDominio reto) {
        reto.setId(null);
        RetoDominio creado = mapper.toDominio(retos.guardar(mapper.toEntity(reto)));
        return ResponseEntity.created(URI.create("/api/retos/" + creado.getId())).body(creado);
    }

    @Operation(
            summary = "Eliminar un reto",
            description = """
                    Borra el reto y, en cascada, sus casos de prueba asociados.

                    Se rechaza con `409` si alguna lección de los cursos apunta a este reto, porque entre
                    módulos no hay llaves foráneas que lo impidan y el borrado dejaría la lección
                    apuntando a un reto inexistente.""")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reto eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe un reto con ese identificador"),
            @ApiResponse(responseCode = "409", description = "Una lección de los cursos todavía usa este reto")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del reto a eliminar") @PathVariable long id) {
        if (retos.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (cursos.algunaLeccionUsaReto(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        retos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
