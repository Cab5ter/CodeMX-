package mx.codemx.modules.ranking;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import mx.codemx.modules.retos.RetosApi;
import mx.codemx.modules.usuarios.Usuario;
import mx.codemx.modules.usuarios.UsuariosApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del módulo Ranking. Consulta los puntos del reto al módulo Retos y el
 * nombre de cada jugador al módulo Usuarios, siempre por sus interfaces públicas.
 */
@Service
@Transactional
public class RankingService implements RankingApi {

    private final RankingRepository repo;
    private final RetosApi retos;
    private final UsuariosApi usuarios;

    public RankingService(RankingRepository repo, RetosApi retos, UsuariosApi usuarios) {
        this.repo = repo;
        this.retos = retos;
        this.usuarios = usuarios;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntradaRanking> obtenerRanking() {
        return repo.findAllByOrderByPuntajeTotalDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntradaRankingDto> obtenerRankingConNombres() {
        List<EntradaRanking> entradas = repo.findAllByOrderByPuntajeTotalDesc();
        Map<Long, String> nombres = usuarios.listarTodos().stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombre, (a, b) -> a));

        return entradas.stream()
                .map(e -> {
                    String nombre = nombres.get(e.getUsuarioId());
                    if (nombre == null || nombre.isBlank()) {
                        nombre = "Usuario #" + e.getUsuarioId();
                    }
                    return new EntradaRankingDto(
                            e.getId(), e.getUsuarioId(), nombre, e.getPuntajeTotal(), e.getRetosResueltos());
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EntradaRanking> obtenerPorUsuario(long usuarioId) {
        return repo.findByUsuarioId(usuarioId);
    }

    @Override
    public void registrarAcierto(long usuarioId, long retoId) {
        int puntos = retos.puntosPorReto(retoId);

        EntradaRanking entrada = repo.findByUsuarioId(usuarioId)
                .orElseGet(() -> new EntradaRanking(usuarioId));
        entrada.setPuntajeTotal(entrada.getPuntajeTotal() + puntos);
        entrada.setRetosResueltos(entrada.getRetosResueltos() + 1);
        entrada.setActualizadoEn(Instant.now());

        repo.save(entrada);
    }

    @Override
    public void registrarResultadoDuelo(long ganadorId, long perdedorId, int puntosGanador, int puntosPerdedor) {
        Instant ahora = Instant.now();

        EntradaRanking ganador = repo.findByUsuarioId(ganadorId)
                .orElseGet(() -> new EntradaRanking(ganadorId));
        ganador.setPuntajeTotal(ganador.getPuntajeTotal() + puntosGanador);
        ganador.setActualizadoEn(ahora);
        repo.save(ganador);

        EntradaRanking perdedor = repo.findByUsuarioId(perdedorId)
                .orElseGet(() -> new EntradaRanking(perdedorId));
        perdedor.setPuntajeTotal(Math.max(0, perdedor.getPuntajeTotal() - puntosPerdedor));
        perdedor.setActualizadoEn(ahora);
        repo.save(perdedor);
    }
}
