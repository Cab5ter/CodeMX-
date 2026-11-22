package mx.codemx.modules.envios;

/** Datos del evento que el módulo Envíos publica cuando un envío es ACEPTADO. */
public record EnvioAceptadoEvent(long usuarioId, long retoId) {
}
