package mx.codemx.modules.duelos;

/** Estados por los que pasa un duelo 1 vs 1. */
public enum EstadoDuelo {
    /** Ambos jugadores recibieron el problema y compiten. */
    EN_CURSO,
    /** Alguien resolvió primero (o hubo abandono). */
    TERMINADO
}
