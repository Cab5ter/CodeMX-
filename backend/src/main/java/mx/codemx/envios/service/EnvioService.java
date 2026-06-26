package mx.codemx.envios.service;

import mx.codemx.envios.model.Envio;
import mx.codemx.envios.model.Veredicto;
import mx.codemx.envios.repository.EnvioRepository;
import mx.codemx.evaluacion.model.ResultadoEvaluacion;
import mx.codemx.evaluacion.model.SolicitudEvaluacion;
import mx.codemx.evaluacion.service.EvaluacionService;
import mx.codemx.ranking.service.RankingService;
import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.service.RetoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final EvaluacionService evaluacionService;
    private final RankingService rankingService;
    private final RetoService retoService;

    public EnvioService(EnvioRepository envioRepository,
                        EvaluacionService evaluacionService,
                        RankingService rankingService,
                        RetoService retoService) {
        this.envioRepository = envioRepository;
        this.evaluacionService = evaluacionService;
        this.rankingService = rankingService;
        this.retoService = retoService;
    }

    public Envio enviar(Envio envio) {
        envio.setVeredicto(Veredicto.PENDIENTE);
        Envio guardado = envioRepository.save(envio);

        try {
            SolicitudEvaluacion solicitud = new SolicitudEvaluacion(envio.getRetoId(), envio.getCodigoFuente());
            ResultadoEvaluacion resultado = evaluacionService.evaluar(solicitud);

            Veredicto veredicto = Veredicto.valueOf(resultado.veredicto());

            boolean esPrimerAceptado = veredicto == Veredicto.ACEPTADO
                    && !envioRepository.existsByUsuarioIdAndRetoIdAndVeredicto(
                            envio.getUsuarioId(), envio.getRetoId(), Veredicto.ACEPTADO);

            guardado.setVeredicto(veredicto);
            guardado = envioRepository.save(guardado);

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
