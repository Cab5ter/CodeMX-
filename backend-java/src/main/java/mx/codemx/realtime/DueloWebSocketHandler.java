package mx.codemx.realtime;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mx.codemx.modules.duelos.Duelo;
import mx.codemx.modules.duelos.DueloService;
import mx.codemx.modules.duelos.DuelosApi;
import mx.codemx.modules.duelos.GeneradorProblemas;
import mx.codemx.modules.duelos.ProblemaDuelo;
import mx.codemx.modules.evaluacion.ResultadoEvaluacion;
import mx.codemx.modules.retos.Dificultad;
import mx.codemx.modules.usuarios.Usuario;
import mx.codemx.modules.usuarios.UsuariosApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Canal de tiempo real del modo 1 vs 1. Maneja el emparejamiento, el envío de soluciones
 * (que se evalúan en el servidor contra los casos del problema), el cierre del duelo con
 * puntos y el chat en vivo entre los dos jugadores.
 *
 * <p>Protocolo: JSON por WebSocket, con la misma forma en ambos sentidos —
 * <code>{"target": "...", "arguments": [...]}</code>. El cliente
 * (<code>frontend/src/api/duelosHub.js</code>) expone sobre esto una superficie
 * <code>on/invoke/start/stop</code>, así que los componentes no hablan del protocolo.
 *
 * <p>Mensajes que el cliente puede invocar: {@code BuscarDuelo}, {@code EnviarSolucion},
 * {@code EnviarMensaje}, {@code Escribiendo}.
 *
 * <p>Eventos que emite hacia el cliente:
 * <ul>
 *   <li>{@code EnEspera()} → estás en la cola, esperando rival</li>
 *   <li>{@code DueloIniciado(dto)} → hay rival y problema; empieza el duelo</li>
 *   <li>{@code ResultadoEnvio({veredicto,mensajeError})} → resultado de TU envío</li>
 *   <li>{@code RivalFallo({usuarioId,veredicto})} → tu rival envió y falló</li>
 *   <li>{@code RivalEscribiendo({usuarioId})} → tu rival está escribiendo en el chat</li>
 *   <li>{@code MensajeRecibido({usuarioId,...})} → mensaje de chat</li>
 *   <li>{@code DueloTerminado({ganadorId,...})} → alguien ganó (o hubo abandono)</li>
 *   <li>{@code DueloNoDisponible()} → el duelo ya no existe</li>
 * </ul>
 */
@Component
public class DueloWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(DueloWebSocketHandler.class);

    private final MatchmakingService matchmaking;
    private final GeneradorProblemas generador;
    private final DuelosApi duelos;
    private final UsuariosApi usuarios;
    private final ObjectMapper json;

    /** Conexiones vivas, para poder emitir a los dos jugadores de un duelo. */
    private final Map<String, WebSocketSession> sesiones = new ConcurrentHashMap<>();

    public DueloWebSocketHandler(MatchmakingService matchmaking, GeneradorProblemas generador,
                                 DuelosApi duelos, UsuariosApi usuarios, ObjectMapper json) {
        this.matchmaking = matchmaking;
        this.generador = generador;
        this.duelos = duelos;
        this.usuarios = usuarios;
        this.json = json;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sesiones.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode sobre = json.readTree(message.getPayload());
        String target = sobre.path("target").asString("");
        JsonNode args = sobre.path("arguments");

        switch (target) {
            case "BuscarDuelo" -> buscarDuelo(session,
                    args.path(0).asLong(),
                    args.path(1).asString(""),
                    args.path(2).asString(""));
            case "EnviarSolucion" -> enviarSolucion(session,
                    args.path(0).asLong(),
                    args.path(1).asLong(),
                    args.path(2).asString(""));
            case "EnviarMensaje" -> enviarMensaje(
                    args.path(0).asLong(),
                    args.path(1).asLong(),
                    args.path(2).asText(""));
            case "Escribiendo" -> escribiendo(session,
                    args.path(0).asLong(),
                    args.path(1).asLong());
            default -> log.warn("Mensaje de duelo desconocido: {}", target);
        }
    }

    /** Entra a la cola con una dificultad. Si hay rival esperando (misma dificultad), crea el duelo. */
    private void buscarDuelo(WebSocketSession session, long usuarioId, String nombre, String dificultad) {
        Dificultad nivel = parsearDificultad(dificultad);
        String nombreReal = resolverNombre(usuarioId, nombre);

        JugadorEnEspera yo = new JugadorEnEspera(session.getId(), usuarioId, nombreReal, nivel);

        JugadorEnEspera rival = matchmaking.emparejarOEncolar(yo);
        if (rival == null) {
            enviar(session, "EnEspera");
            return;
        }

        // Soy el segundo en llegar: genero el problema (de la dificultad acordada) y abro el duelo.
        ProblemaDuelo problema = generador.generar(nivel);
        Duelo duelo = duelos.crear(rival.usuarioId(), usuarioId, problema.titulo(), nivel);

        DueloActivo activo = new DueloActivo(duelo.getId(), rival, yo, problema);
        matchmaking.registrarDuelo(activo);

        DueloService.PuntosDuelo puntos = DueloService.puntosPorDificultad(nivel);

        // El payload NUNCA incluye los casos de prueba (sólo enunciado y un ejemplo).
        DueloIniciadoDto dto = new DueloIniciadoDto(
                duelo.getId(),
                problema.titulo(),
                problema.enunciado(),
                problema.ejemploEntrada(),
                problema.ejemploSalida(),
                nivel.name(),
                puntos.ganar(),
                puntos.perder(),
                List.of(new JugadorDto(rival.usuarioId(), rival.nombre()),
                        new JugadorDto(yo.usuarioId(), yo.nombre())));

        enviarAGrupo(activo, "DueloIniciado", dto);
    }

    /** Evalúa una solución. El primero en lograr ACEPTADO gana el duelo. */
    private void enviarSolucion(WebSocketSession session, long dueloId, long usuarioId, String codigo) {
        DueloActivo activo = matchmaking.obtener(dueloId);
        if (activo == null) {
            enviar(session, "DueloNoDisponible");
            return;
        }

        ResultadoEvaluacion resultado = duelos.evaluar(codigo, activo.getProblema().casos());

        if ("ACEPTADO".equals(resultado.veredicto()) && activo.intentarGanar(usuarioId)) {
            duelos.registrarGanador(dueloId, usuarioId);
            enviarAGrupo(activo, "DueloTerminado",
                    new DueloTerminadoDto(usuarioId, activo.nombreDe(usuarioId), "resuelto"));
            matchmaking.quitar(dueloId);
            return;
        }

        // No ganó: o falló, o alguien ya había ganado.
        enviar(session, "ResultadoEnvio",
                new ResultadoEnvioDto(resultado.veredicto(), resultado.mensajeError()));

        if (!"ACEPTADO".equals(resultado.veredicto())) {
            enviarAGrupoExcepto(activo, session.getId(), "RivalFallo",
                    new RivalFalloDto(usuarioId, resultado.veredicto()));
        }
    }

    /** Chat en vivo entre los dos jugadores del duelo. */
    private void enviarMensaje(long dueloId, long usuarioId, String texto) {
        DueloActivo activo = matchmaking.obtener(dueloId);
        if (activo == null || texto.isBlank()) {
            return;
        }

        enviarAGrupo(activo, "MensajeRecibido",
                new MensajeDto(usuarioId, activo.nombreDe(usuarioId), texto.trim(), Instant.now()));
    }

    /** Indicador "está escribiendo…". */
    private void escribiendo(WebSocketSession session, long dueloId, long usuarioId) {
        DueloActivo activo = matchmaking.obtener(dueloId);
        if (activo == null) {
            return;
        }
        enviarAGrupoExcepto(activo, session.getId(), "RivalEscribiendo", new UsuarioDto(usuarioId));
    }

    /** Si alguien abandona un duelo en curso, el rival gana por abandono. */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sesiones.remove(session.getId());

        DueloActivo activo = matchmaking.quitarConexion(session.getId());
        if (activo == null) {
            return;
        }

        JugadorEnEspera rival = activo.rivalDe(session.getId());
        if (activo.intentarGanar(rival.usuarioId())) {
            duelos.registrarGanador(activo.getDueloId(), rival.usuarioId());
            enviarAGrupo(activo, "DueloTerminado",
                    new DueloTerminadoDto(rival.usuarioId(), rival.nombre(), "abandono"));
            matchmaking.quitar(activo.getDueloId());
        }
    }

    // ---- helpers ----

    private static Dificultad parsearDificultad(String valor) {
        try {
            return Dificultad.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Dificultad.INTERMEDIO;
        }
    }

    private String resolverNombre(long usuarioId, String nombreFallback) {
        String nombre = usuarios.buscarPorId(usuarioId).map(Usuario::getNombre).orElse(null);
        if (nombre != null && !nombre.isBlank()) {
            return nombre;
        }
        return nombreFallback == null || nombreFallback.isBlank()
                ? "Usuario #" + usuarioId
                : nombreFallback;
    }

    private void enviar(WebSocketSession session, String target) {
        enviar(session, target, null);
    }

    private void enviar(WebSocketSession session, String target, Object payload) {
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            List<Object> argumentos = payload == null ? List.of() : List.of(payload);
            String texto = json.writeValueAsString(Map.of("target", target, "arguments", argumentos));
            // WebSocketSession no admite envíos concurrentes: hay que serializarlos por sesión.
            synchronized (session) {
                session.sendMessage(new TextMessage(texto));
            }
        } catch (Exception e) {
            log.warn("No se pudo enviar '{}' a la sesión {}", target, session.getId(), e);
        }
    }

    private void enviarAGrupo(DueloActivo activo, String target, Object payload) {
        enviar(sesiones.get(activo.getJugador1().connectionId()), target, payload);
        enviar(sesiones.get(activo.getJugador2().connectionId()), target, payload);
    }

    private void enviarAGrupoExcepto(DueloActivo activo, String exceptoConnectionId,
                                     String target, Object payload) {
        for (JugadorEnEspera jugador : List.of(activo.getJugador1(), activo.getJugador2())) {
            if (!jugador.connectionId().equals(exceptoConnectionId)) {
                enviar(sesiones.get(jugador.connectionId()), target, payload);
            }
        }
    }

    // ---- payloads hacia el cliente ----

    private record JugadorDto(long id, String nombre) {
    }

    private record DueloIniciadoDto(
            long dueloId, String titulo, String enunciado, String ejemploEntrada, String ejemploSalida,
            String dificultad, int puntosGanar, int puntosPerder, List<JugadorDto> jugadores) {
    }

    private record ResultadoEnvioDto(String veredicto, String mensajeError) {
    }

    private record RivalFalloDto(long usuarioId, String veredicto) {
    }

    private record UsuarioDto(long usuarioId) {
    }

    private record MensajeDto(long usuarioId, String nombre, String texto, Instant ts) {
    }

    private record DueloTerminadoDto(long ganadorId, String ganadorNombre, String motivo) {
    }
}
