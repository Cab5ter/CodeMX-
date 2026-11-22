package mx.codemx.modules.envios;

import java.util.List;
import java.util.Optional;
import mx.codemx.modules.evaluacion.EvaluacionApi;
import mx.codemx.modules.evaluacion.ResultadoEvaluacion;
import mx.codemx.modules.evaluacion.SolicitudEvaluacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnvioService implements EnviosApi {

    private final EnvioRepository repo;
    private final EvaluacionApi evaluacion;
    private final List<EnvioObserver> observers;

    public EnvioService(EnvioRepository repo, EvaluacionApi evaluacion, List<EnvioObserver> observers) {
        this.repo = repo;
        this.evaluacion = evaluacion;
        this.observers = observers;
    }

    @Override
    public Envio enviar(Envio envio) {
        envio.setVeredicto(Veredicto.PENDIENTE);
        Envio guardado = repo.save(envio);

        try {
            SolicitudEvaluacion solicitud =
                    new SolicitudEvaluacion(envio.getRetoId(), envio.getCodigoFuente());
            ResultadoEvaluacion resultado = evaluacion.evaluar(solicitud);

            Veredicto veredicto = Veredicto.valueOf(resultado.veredicto());

            boolean esPrimerAceptado = veredicto == Veredicto.ACEPTADO
                    && !tieneAceptado(envio.getUsuarioId(), envio.getRetoId());

            guardado.setVeredicto(veredicto);
            guardado = repo.save(guardado);

            if (esPrimerAceptado) {
                EnvioAceptadoEvent evento =
                        new EnvioAceptadoEvent(envio.getUsuarioId(), envio.getRetoId());
                for (EnvioObserver observer : observers) {
                    observer.onEnvioAceptado(evento);
                }
            }
        } catch (Exception e) {
            guardado.setVeredicto(Veredicto.ERROR_EN_EJECUCION);
            guardado = repo.save(guardado);
        }

        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Envio> buscarPorId(long id) {
        return repo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Envio> listarPorUsuario(long usuarioId) {
        return repo.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Envio> listarPorReto(long retoId) {
        return repo.findByRetoId(retoId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneAceptado(long usuarioId, long retoId) {
        return repo.existsByUsuarioIdAndRetoIdAndVeredicto(usuarioId, retoId, Veredicto.ACEPTADO);
    }
}
