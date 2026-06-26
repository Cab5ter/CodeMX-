package mx.codemx.retos.model;

/**
 * Cuerpo de la petición POST /api/retos/{id}/submit.
 * El retoId viaja en la URL, así que aquí solo van el usuario y el código.
 */
public record EnvioRequest(Long usuarioId, String codigoFuente) {}
