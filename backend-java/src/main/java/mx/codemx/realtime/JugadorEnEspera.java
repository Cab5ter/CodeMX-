package mx.codemx.realtime;

import mx.codemx.modules.retos.Dificultad;

/** Un jugador esperando rival en la cola de emparejamiento, con la dificultad que eligió. */
public record JugadorEnEspera(String connectionId, long usuarioId, String nombre, Dificultad dificultad) {
}
