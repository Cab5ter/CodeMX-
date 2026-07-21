package mx.codemx.modules.envios;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Envíos. La consume el gateway cuando el estudiante manda
 * una solución. Internamente coordina con Evaluación y Ranking vía sus interfaces.
 */
public interface EnviosApi {

    Envio enviar(Envio envio);

    Optional<Envio> buscarPorId(long id);

    List<Envio> listarPorUsuario(long usuarioId);

    List<Envio> listarPorReto(long retoId);

    /** True si el usuario ya resolvió (ACEPTADO) ese reto. Lo usa el módulo Cursos. */
    boolean tieneAceptado(long usuarioId, long retoId);
}
