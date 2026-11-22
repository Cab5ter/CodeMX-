package mx.codemx.realtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class MatchmakingService {

    private final Object lock = new Object();
    private final List<JugadorEnEspera> espera = new ArrayList<>();
    private final Map<Long, DueloActivo> activos = new ConcurrentHashMap<>();

    public JugadorEnEspera emparejarOEncolar(JugadorEnEspera jugador) {
        synchronized (lock) {
            espera.removeIf(j -> j.connectionId().equals(jugador.connectionId())
                    || j.usuarioId() == jugador.usuarioId());

            for (int i = 0; i < espera.size(); i++) {
                JugadorEnEspera candidato = espera.get(i);
                if (candidato.usuarioId() != jugador.usuarioId()
                        && candidato.dificultad() == jugador.dificultad()) {
                    espera.remove(i);
                    return candidato;
                }
            }

            espera.add(jugador);
            return null;
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
