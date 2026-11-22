package mx.codemx.realtime;

import mx.codemx.modules.retos.Dificultad;

public record JugadorEnEspera(String connectionId, long usuarioId, String nombre, Dificultad dificultad) {
}
