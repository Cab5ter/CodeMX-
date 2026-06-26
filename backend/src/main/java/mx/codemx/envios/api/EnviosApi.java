package mx.codemx.envios.api;

import mx.codemx.envios.model.Envio;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Envíos.
 *
 * El gateway la consume cuando el estudiante manda una solución. Internamente,
 * Envíos coordina el flujo: guarda el envío, pide la evaluación al módulo Evaluación
 * (vía EvaluacionApi) y, si el veredicto es ACEPTADO por primera vez, notifica al
 * módulo Ranking (vía RankingApi). No accede a los internals de esos módulos.
 */
public interface EnviosApi {

    Envio enviar(Envio envio);

    Optional<Envio> buscarPorId(Long id);

    List<Envio> listarPorUsuario(Long usuarioId);

    List<Envio> listarPorReto(Long retoId);

    /** True si el usuario ya resolvió (ACEPTADO) ese reto. Lo usa el módulo Cursos. */
    boolean tieneAceptado(Long usuarioId, Long retoId);
}
