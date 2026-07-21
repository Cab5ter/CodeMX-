package mx.codemx.modules.cursos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mx.codemx.modules.cursos.CursosDtos.LeccionDetalle;
import mx.codemx.modules.cursos.CursosDtos.LeccionVista;
import mx.codemx.modules.cursos.CursosDtos.ModuloDetalle;
import mx.codemx.modules.cursos.CursosDtos.ModuloResumen;
import mx.codemx.modules.cursos.CursosDtos.PreguntaVista;
import mx.codemx.modules.cursos.CursosDtos.ResultadoExamen;
import mx.codemx.modules.cursos.CursosExceptions.ExamenBloqueadoException;
import mx.codemx.modules.cursos.CursosExceptions.RecursoNoEncontradoException;
import mx.codemx.modules.envios.EnviosApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Módulo Cursos: organiza el aprendizaje en módulos con lecciones de teoría y ejercicios.
 *
 * <p>Reglas:
 * <ul>
 *   <li>Una lección de TEORIA se completa cuando el alumno la marca como leída.</li>
 *   <li>Una lección de EJERCICIO se completa cuando el alumno obtiene ACEPTADO en su reto
 *       (se consulta al módulo Envíos vía EnviosApi, sin duplicar estado).</li>
 *   <li>El examen se desbloquea al completar al menos UMBRAL_EXAMEN % de las lecciones.</li>
 * </ul>
 */
@Service
@Transactional
public class CursoService implements CursosApi {

    /** % de lecciones para desbloquear el examen. */
    public static final int UMBRAL_EXAMEN = 70;
    /** % de aciertos para aprobar. */
    public static final int UMBRAL_APROBACION = 70;

    private final ModuloRepository modulos;
    private final LeccionRepository lecciones;
    private final ProgresoLeccionRepository progreso;
    private final PreguntaExamenRepository preguntas;
    private final EnviosApi envios;

    public CursoService(ModuloRepository modulos, LeccionRepository lecciones,
                        ProgresoLeccionRepository progreso, PreguntaExamenRepository preguntas,
                        EnviosApi envios) {
        this.modulos = modulos;
        this.lecciones = lecciones;
        this.progreso = progreso;
        this.preguntas = preguntas;
        this.envios = envios;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuloResumen> listarModulos(Long usuarioId) {
        List<ModuloResumen> resumenes = new ArrayList<>();

        for (Modulo m : modulos.findAllByOrderByOrdenAsc()) {
            List<Leccion> delModulo = lecciones.findByModuloIdOrderByOrdenAsc(m.getId());
            int total = delModulo.size();
            int completadas = 0;
            for (Leccion l : delModulo) {
                if (estaCompletada(l, usuarioId)) {
                    completadas++;
                }
            }

            int avance = total == 0 ? 0 : completadas * 100 / total;
            resumenes.add(new ModuloResumen(m.getId(), m.getTitulo(), m.getDescripcion(), m.getIcono(),
                    total, completadas, avance, avance >= UMBRAL_EXAMEN));
        }
        return resumenes;
    }

    @Override
    @Transactional(readOnly = true)
    public ModuloDetalle obtenerModulo(long moduloId, Long usuarioId) {
        Modulo m = modulos.findById(moduloId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Módulo no encontrado"));

        List<LeccionVista> vistas = new ArrayList<>();
        for (Leccion l : lecciones.findByModuloIdOrderByOrdenAsc(moduloId)) {
            vistas.add(new LeccionVista(l.getId(), l.getTitulo(), l.getTipo(), l.getRetoId(),
                    estaCompletada(l, usuarioId), l.getOrden()));
        }

        int total = vistas.size();
        int completadas = (int) vistas.stream().filter(LeccionVista::completada).count();
        int avance = total == 0 ? 0 : completadas * 100 / total;

        return new ModuloDetalle(m.getId(), m.getTitulo(), m.getDescripcion(), m.getIcono(),
                vistas, total, completadas, avance, UMBRAL_EXAMEN, avance >= UMBRAL_EXAMEN);
    }

    @Override
    @Transactional(readOnly = true)
    public LeccionDetalle obtenerLeccion(long leccionId, Long usuarioId) {
        Leccion l = lecciones.findById(leccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lección no encontrada"));

        // La relación evita la consulta extra: el módulo se navega desde la propia lección.
        Modulo m = l.getModulo();

        return new LeccionDetalle(l.getId(), m.getId(), m.getTitulo(), l.getTitulo(), l.getTipo(),
                l.getContenido(), l.getEjemploCodigo(), l.getRetoId(), estaCompletada(l, usuarioId));
    }

    @Override
    public void completarLeccion(long leccionId, long usuarioId) {
        Leccion l = lecciones.findById(leccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lección no encontrada"));

        if (l.getTipo() != TipoLeccion.TEORIA) {
            throw new IllegalArgumentException(
                    "Las lecciones de ejercicio se completan resolviendo el reto");
        }

        if (!progreso.existsByUsuarioIdAndLeccionId(usuarioId, leccionId)) {
            progreso.save(new ProgresoLeccion(usuarioId, l));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreguntaVista> obtenerExamen(long moduloId, long usuarioId) {
        exigirExamenDesbloqueado(moduloId, usuarioId);

        return preguntas.findByModuloIdOrderByOrdenAsc(moduloId).stream()
                .map(p -> new PreguntaVista(p.getId(), p.getEnunciado(),
                        List.of(p.getOpcionA(), p.getOpcionB(), p.getOpcionC(), p.getOpcionD())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoExamen calificarExamen(long moduloId, long usuarioId, Map<Long, Integer> respuestas) {
        exigirExamenDesbloqueado(moduloId, usuarioId);

        List<PreguntaExamen> delModulo = preguntas.findByModuloIdOrderByOrdenAsc(moduloId);
        if (delModulo.isEmpty()) {
            throw new RecursoNoEncontradoException("El módulo no tiene examen configurado");
        }

        int aciertos = (int) delModulo.stream()
                .filter(p -> {
                    Integer elegida = respuestas.get(p.getId());
                    return elegida != null && elegida == p.getCorrecta();
                })
                .count();

        int total = delModulo.size();
        int porcentaje = aciertos * 100 / total;
        return new ResultadoExamen(aciertos, total, porcentaje, porcentaje >= UMBRAL_APROBACION);
    }

    // ---- helpers ----

    private void exigirExamenDesbloqueado(long moduloId, long usuarioId) {
        if (!obtenerModulo(moduloId, usuarioId).examenDesbloqueado()) {
            throw new ExamenBloqueadoException("Debes completar al menos " + UMBRAL_EXAMEN
                    + "% de las lecciones para presentar el examen");
        }
    }

    private boolean estaCompletada(Leccion leccion, Long usuarioId) {
        if (usuarioId == null) {
            return false;
        }
        if (leccion.getTipo() == TipoLeccion.EJERCICIO) {
            return leccion.getRetoId() != null && envios.tieneAceptado(usuarioId, leccion.getRetoId());
        }
        return progreso.existsByUsuarioIdAndLeccionId(usuarioId, leccion.getId());
    }
}
