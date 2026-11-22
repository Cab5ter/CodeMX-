package mx.codemx.modules.duelos;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import mx.codemx.modules.evaluacion.EvaluadorStrategyFactory;
import mx.codemx.modules.evaluacion.ResultadoEvaluacion;
import mx.codemx.modules.evaluacion.SolicitudEvaluacion;
import mx.codemx.modules.evaluacion.TipoEvaluacion;
import mx.codemx.modules.ranking.RankingApi;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.Dificultad;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DueloService implements DuelosApi {

    public record PuntosDuelo(int ganar, int perder) {
    }

    public static PuntosDuelo puntosPorDificultad(Dificultad dificultad) {
        return switch (dificultad) {
            case BASICO -> new PuntosDuelo(15, 5);
            case INTERMEDIO -> new PuntosDuelo(25, 10);
            case AVANZADO -> new PuntosDuelo(40, 20);
        };
    }

    private final DueloRepository repo;
    private final EvaluadorStrategyFactory evaluadores;
    private final RankingApi ranking;

    public DueloService(DueloRepository repo, EvaluadorStrategyFactory evaluadores, RankingApi ranking) {
        this.repo = repo;
        this.evaluadores = evaluadores;
        this.ranking = ranking;
    }

    @Override
    public Duelo crear(long jugador1Id, long jugador2Id, String titulo, Dificultad dificultad) {
        Duelo duelo = new Duelo();
        duelo.setJugador1Id(jugador1Id);
        duelo.setJugador2Id(jugador2Id);
        duelo.setTitulo(titulo);
        duelo.setDificultad(dificultad);
        duelo.setEstado(EstadoDuelo.EN_CURSO);
        duelo.setCreadoEn(Instant.now());
        return repo.save(duelo);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoEvaluacion evaluar(String codigoFuente, List<CasoPrueba> casos) {
        return evaluadores.crear(TipoEvaluacion.LOCAL)
                .ejecutar(new SolicitudEvaluacion(0, codigoFuente), casos);
    }

    @Override
    public void registrarGanador(long dueloId, long ganadorId) {
        Optional<Duelo> encontrado = repo.findById(dueloId);
        if (encontrado.isEmpty()) {
            return;
        }

        Duelo duelo = encontrado.get();
        if (duelo.getEstado() == EstadoDuelo.TERMINADO) {
            return;
        }

        long perdedorId = duelo.getJugador1Id() == ganadorId ? duelo.getJugador2Id() : duelo.getJugador1Id();

        duelo.setGanadorId(ganadorId);
        duelo.setEstado(EstadoDuelo.TERMINADO);
        duelo.setTerminadoEn(Instant.now());
        repo.save(duelo);

        PuntosDuelo puntos = puntosPorDificultad(duelo.getDificultad());
        ranking.registrarResultadoDuelo(ganadorId, perdedorId, puntos.ganar(), puntos.perder());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Duelo> historial(long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }
}
