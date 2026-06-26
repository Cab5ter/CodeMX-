package mx.codemx.retos.service;

import mx.codemx.evaluador.model.ResultadoEvaluacion;
import mx.codemx.evaluador.model.SolicitudEvaluacion;
import mx.codemx.evaluador.service.EvaluadorService;
import mx.codemx.ranking.service.RankingService;
import mx.codemx.retos.model.Envio;
import mx.codemx.retos.model.Veredicto;
import mx.codemx.retos.repository.EnvioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Gestión de envíos dentro del módulo Retos.
 *
 * Implementa el flujo de la Vista de Procesos del ADR-02:
 *   1. Guarda el envío como PENDIENTE en PostgreSQL.
 *   2. Pide al módulo Evaluador que ejecute el código contra los casos de prueba.
 *   3. Actualiza el envío con el veredicto (AC / WA / TLE).
 *   4. Si es el primer ACEPTADO del usuario en ese reto, dispara la actualización del ranking.
 */
@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final EvaluadorService evaluadorService;
    private final RankingService rankingService;
    private final RetoService retoService;

    public EnvioService(EnvioRepository envioRepository,
                        EvaluadorService evaluadorService,
                        RankingService rankingService,
                        RetoService retoService) {
        this.envioRepository = envioRepository;
        this.evaluadorService = evaluadorService;
        this.rankingService = rankingService;
        this.retoService = retoService;
    }

    public Envio enviar(Envio envio) {
        // 1. Guarda el envío pendiente
        envio.setVeredicto(Veredicto.PENDIENTE);
        Envio guardado = envioRepository.save(envio);

        try {
            // 2. Pide la evaluación al módulo Evaluador
            SolicitudEvaluacion solicitud = new SolicitudEvaluacion(envio.getRetoId(), envio.getCodigoFuente());
            ResultadoEvaluacion resultado = evaluadorService.evaluar(solicitud);

            Veredicto veredicto = Veredicto.valueOf(resultado.veredicto());

            boolean esPrimerAceptado = veredicto == Veredicto.ACEPTADO
                    && !envioRepository.existsByUsuarioIdAndRetoIdAndVeredicto(
                            envio.getUsuarioId(), envio.getRetoId(), Veredicto.ACEPTADO);

            // 3. Actualiza el resultado del envío
            guardado.setVeredicto(veredicto);
            guardado = envioRepository.save(guardado);

            // 4. Dispara la actualización del ranking
            if (esPrimerAceptado) {
                int puntos = calcularPuntos(envio.getRetoId());
                rankingService.sumarPuntaje(envio.getUsuarioId(), puntos);
            }

        } catch (Exception e) {
            guardado.setVeredicto(Veredicto.ERROR_EN_EJECUCION);
            envioRepository.save(guardado);
        }

        return guardado;
    }

    public Optional<Envio> buscarPorId(Long id) {
        return envioRepository.findById(id);
    }

    public List<Envio> listarPorUsuario(Long usuarioId) {
        return envioRepository.findByUsuarioId(usuarioId);
    }

    public List<Envio> listarPorReto(Long retoId) {
        return envioRepository.findByRetoId(retoId);
    }

    private int calcularPuntos(Long retoId) {
        return retoService.buscarPorId(retoId)
                .map(reto -> switch (reto.getDificultad()) {
                    case BASICO -> 10;
                    case INTERMEDIO -> 25;
                    case AVANZADO -> 50;
                })
                .orElse(10);
    }
}
