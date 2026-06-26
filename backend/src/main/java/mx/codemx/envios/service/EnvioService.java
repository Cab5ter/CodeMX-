package mx.codemx.envios.service;

import mx.codemx.envios.api.EnviosApi;
import mx.codemx.envios.model.Envio;
import mx.codemx.envios.model.Veredicto;
import mx.codemx.envios.repository.EnvioRepository;
import mx.codemx.evaluacion.api.EvaluacionApi;
import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;
import mx.codemx.ranking.api.RankingApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Coordina el flujo de un envío (ADR-03):
 *   1. Guarda el envío como PENDIENTE.
 *   2. Pide la evaluación al módulo Evaluación (vía EvaluacionApi).
 *   3. Actualiza el veredicto.
 *   4. Si es el primer ACEPTADO del usuario en ese reto, notifica al módulo Ranking
 *      (vía RankingApi), que a su vez consulta los puntos al módulo Retos.
 *
 * Envíos sólo conoce las interfaces de Evaluación y Ranking, no sus internals.
 */
@Service
public class EnvioService implements EnviosApi {

    private final EnvioRepository envioRepository;
    private final EvaluacionApi evaluacionApi;
    private final RankingApi rankingApi;

    public EnvioService(EnvioRepository envioRepository,
                        EvaluacionApi evaluacionApi,
                        RankingApi rankingApi) {
        this.envioRepository = envioRepository;
        this.evaluacionApi = evaluacionApi;
        this.rankingApi = rankingApi;
    }

    @Override
    public Envio enviar(Envio envio) {
        envio.setVeredicto(Veredicto.PENDIENTE);
        Envio guardado = envioRepository.save(envio);

        try {
            SolicitudEvaluacion solicitud = new SolicitudEvaluacion(envio.getRetoId(), envio.getCodigoFuente());
            ResultadoEvaluacion resultado = evaluacionApi.evaluar(solicitud);

            Veredicto veredicto = Veredicto.valueOf(resultado.veredicto());

            boolean esPrimerAceptado = veredicto == Veredicto.ACEPTADO
                    && !envioRepository.existsByUsuarioIdAndRetoIdAndVeredicto(
                            envio.getUsuarioId(), envio.getRetoId(), Veredicto.ACEPTADO);

            guardado.setVeredicto(veredicto);
            guardado = envioRepository.save(guardado);

            if (esPrimerAceptado) {
                rankingApi.registrarAcierto(envio.getUsuarioId(), envio.getRetoId());
            }

        } catch (Exception e) {
            guardado.setVeredicto(Veredicto.ERROR_EN_EJECUCION);
            envioRepository.save(guardado);
        }

        return guardado;
    }

    @Override
    public Optional<Envio> buscarPorId(Long id) {
        return envioRepository.findById(id);
    }

    @Override
    public List<Envio> listarPorUsuario(Long usuarioId) {
        return envioRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Envio> listarPorReto(Long retoId) {
        return envioRepository.findByRetoId(retoId);
    }

    @Override
    public boolean tieneAceptado(Long usuarioId, Long retoId) {
        return envioRepository.existsByUsuarioIdAndRetoIdAndVeredicto(usuarioId, retoId, Veredicto.ACEPTADO);
    }
}
