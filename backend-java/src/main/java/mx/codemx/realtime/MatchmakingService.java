package mx.codemx.realtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Servicio singleton que gestiona la cola de emparejamiento y los duelos activos en memoria.
 * El monolito corre en una sola instancia, así que un estado en memoria con bloqueo basta.
 * El emparejamiento sólo junta a jugadores que eligieron la <b>misma dificultad</b>.
 */
@Service
public class MatchmakingService {

    private final Object lock = new Object();
    private final List<JugadorEnEspera> espera = new ArrayList<>();
    private final Map<Long, DueloActivo> activos = new ConcurrentHashMap<>();

    /**
     * Mete al jugador a la sala de espera; si ya había alguien (distinto, misma dificultad)
     * esperando, lo empareja y devuelve al rival. Devuelve {@code null} si quedó en espera.
     */
    public JugadorEnEspera emparejarOEncolar(JugadorEnEspera jugador) {
        synchronized (lock) {
            // Evita duplicados de la misma conexión o usuario en la cola.
            espera.removeIf(j -> j.connectionId().equals(jugador.connectionId())
                    || j.usuarioId() == jugador.usuarioId());

            for (int i = 0; i < espera.size(); i++) {
                JugadorEnEspera candidato = espera.get(i);
                if (candidato.usuarioId() != jugador.usuarioId()
                        && candidato.dificultad() == jugador.dificultad()) {
                    espera.remove(i);
                    return candidato; // emparejado
                }
            }

            espera.add(jugador);
            return null; // en espera
        }
    }

    public void registrarDuelo(DueloActivo duelo) {
        activos.put(duelo.getDueloId(), duelo);
    }

    public DueloActivo obtener(long dueloId) {
        return activos.get(dueloId);
    }

    public void quitar(long dueloId) {
        activos.remove(dueloId);
    }

    /**
     * Saca una conexión de la cola y/o de su duelo activo (al desconectarse). Devuelve el
     * duelo activo y aún no terminado en el que estaba, para que el handler avise al rival.
     */
    public DueloActivo quitarConexion(String connectionId) {
        synchronized (lock) {
            espera.removeIf(j -> j.connectionId().equals(connectionId));
        }

        for (DueloActivo duelo : activos.values()) {
            if (duelo.participa(connectionId) && duelo.getGanadorId() == null) {
                return duelo;
            }
        }
        return null;
    }
}
