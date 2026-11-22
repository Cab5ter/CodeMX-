package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import mx.codemx.modules.envios.Envio;
import mx.codemx.modules.envios.EnviosApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/envios")
@Tag(name = "Envios", description = "Envío de código a evaluar contra los casos de prueba de un reto, y consulta del veredicto.")
public class EnviosController {

    private final EnviosApi envios;

    public EnviosController(EnviosApi envios) {
        this.envios = envios;
    }

    @Operation(
            summary = "Enviar código para que sea evaluado",
            description = """
                    Guarda el envío como `PENDIENTE`, lo ejecuta contra los casos de prueba del reto y
                    devuelve el envío ya con su veredicto final. La evaluación es **síncrona**: la
                    respuesta llega cuando el código terminó de correr.

                    Veredictos posibles: `ACEPTADO` (pasó todos los casos), `INCORRECTO` (la salida no
                    coincide en algún caso), `TIEMPO_LIMITE_EXCEDIDO` y `ERROR_EN_EJECUCION`.

                    Cuando es el **primer** envío `ACEPTADO` de ese usuario para ese reto, se publica un
                    evento y el módulo de Ranking suma los puntos correspondientes a la dificultad
                    (patrón Observer). Los envíos aceptados posteriores del mismo reto no vuelven a
                    sumar.

                    El `id` y el `veredicto` los asigna el servidor: si el cuerpo los trae, se ignoran.""")
    @ApiResponse(responseCode = "201", description = "Envío registrado y evaluado; el cuerpo trae el veredicto")
    @PostMapping
    public ResponseEntity<Envio> enviar(@Valid @RequestBody Envio envio) {
        envio.setId(null);
        Envio creado = envios.enviar(envio);
        return ResponseEntity.created(URI.create("/api/envios/" + creado.getId())).body(creado);
    }

    @Operation(
            summary = "Obtener un envío por su identificador",
            description = "Devuelve el código enviado, el veredicto y la marca de tiempo del envío.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un envío con ese identificador")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtener(
            @Parameter(description = "Identificador del envío") @PathVariable long id) {
        return envios.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Listar los envíos de un usuario",
            description = """
                    Devuelve el historial completo de intentos de un estudiante, de todos los retos.
                    Sirve para mostrar su actividad reciente en el perfil.""")
    @ApiResponse(responseCode = "200", description = "Lista de envíos (posiblemente vacía)")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Envio>> porUsuario(
            @Parameter(description = "Identificador del usuario") @PathVariable long usuarioId) {
        return ResponseEntity.ok(envios.listarPorUsuario(usuarioId));
    }

    @Operation(
            summary = "Listar los envíos de un reto",
            description = """
                    Devuelve todos los intentos que se han hecho sobre un reto, de cualquier usuario.
                    Útil para revisar qué tan difícil está resultando un reto en la práctica.""")
    @ApiResponse(responseCode = "200", description = "Lista de envíos (posiblemente vacía)")
    @GetMapping("/reto/{retoId}")
    public ResponseEntity<List<Envio>> porReto(
            @Parameter(description = "Identificador del reto") @PathVariable long retoId) {
        return ResponseEntity.ok(envios.listarPorReto(retoId));
    }
}
