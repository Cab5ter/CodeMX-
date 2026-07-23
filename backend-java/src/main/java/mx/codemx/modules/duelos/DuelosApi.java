package mx.codemx.modules.duelos;

import java.util.List;
import mx.codemx.modules.evaluacion.ResultadoEvaluacion;
import mx.codemx.modules.retos.CasoPrueba;
import mx.codemx.modules.retos.Dificultad;

public interface DuelosApi {

    Duelo crear(long jugador1Id, long jugador2Id, String titulo, Dificultad dificultad);

    ResultadoEvaluacion evaluar(String codigoFuente, List<CasoPrueba> casos);

    void registrarGanador(long dueloId, long ganadorId);

    List<Duelo> historial(long usuarioId);
}
