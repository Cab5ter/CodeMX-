package mx.codemx.realtime;

import mx.codemx.modules.duelos.ProblemaDuelo;

/**
 * Estado en memoria de un duelo en curso: conexiones, jugadores, el problema (con sus
 * casos de prueba, que jamás salen del servidor) y quién ganó. Vive mientras dura el duelo.
 */
public class DueloActivo {

    private final long dueloId;
    private final JugadorEnEspera jugador1;
    private final JugadorEnEspera jugador2;
    private final ProblemaDuelo problema;

    private Long ganadorId;

    public DueloActivo(long dueloId, JugadorEnEspera jugador1, JugadorEnEspera jugador2, ProblemaDuelo problema) {
        this.dueloId = dueloId;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.problema = problema;
    }

    public long getDueloId() {
        return dueloId;
    }

    public JugadorEnEspera getJugador1() {
        return jugador1;
    }

    public JugadorEnEspera getJugador2() {
        return jugador2;
    }

    public ProblemaDuelo getProblema() {
        return problema;
    }

    public synchronized Long getGanadorId() {
        return ganadorId;
    }

    /** Fija al ganador de forma atómica. Devuelve true sólo la primera vez. */
    public synchronized boolean intentarGanar(long usuarioId) {
        if (ganadorId != null) {
            return false;
        }
        ganadorId = usuarioId;
        return true;
    }

    public String nombreDe(long usuarioId) {
        return usuarioId == jugador1.usuarioId() ? jugador1.nombre() : jugador2.nombre();
    }

    /** El rival de la conexión dada. */
    public JugadorEnEspera rivalDe(String connectionId) {
        return jugador1.connectionId().equals(connectionId) ? jugador2 : jugador1;
    }

    public boolean participa(String connectionId) {
        return jugador1.connectionId().equals(connectionId) || jugador2.connectionId().equals(connectionId);
    }
}
