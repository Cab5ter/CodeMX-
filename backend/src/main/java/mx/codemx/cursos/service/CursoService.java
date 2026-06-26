package mx.codemx.cursos.service;

import mx.codemx.cursos.api.CursosApi;
import mx.codemx.cursos.dto.*;
import mx.codemx.cursos.model.Leccion;
import mx.codemx.cursos.model.Modulo;
import mx.codemx.cursos.model.PreguntaExamen;
import mx.codemx.cursos.model.ProgresoLeccion;
import mx.codemx.cursos.model.TipoLeccion;
import mx.codemx.cursos.repository.LeccionRepository;
import mx.codemx.cursos.repository.ModuloRepository;
import mx.codemx.cursos.repository.PreguntaExamenRepository;
import mx.codemx.cursos.repository.ProgresoLeccionRepository;
import mx.codemx.envios.api.EnviosApi;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Módulo Cursos: organiza el aprendizaje en módulos con lecciones de teoría y ejercicios.
 *
 * Reglas:
 *  - Una lección de TEORIA se completa cuando el alumno la marca como leída.
 *  - Una lección de EJERCICIO se completa cuando el alumno obtiene ACEPTADO en su reto
 *    (se consulta al módulo Envíos vía EnviosApi, sin duplicar estado).
 *  - El examen de un módulo se desbloquea al completar al menos UMBRAL_EXAMEN % de sus lecciones.
 */
@Service
public class CursoService implements CursosApi {

    /** Porcentaje mínimo de lecciones completadas para poder presentar el examen. */
    public static final int UMBRAL_EXAMEN = 70;

    /** Porcentaje mínimo de aciertos para aprobar el examen. */
    public static final int UMBRAL_APROBACION = 70;

    private final ModuloRepository moduloRepository;
    private final LeccionRepository leccionRepository;
    private final ProgresoLeccionRepository progresoRepository;
    private final PreguntaExamenRepository preguntaRepository;
    private final EnviosApi enviosApi;   // se consulta por interfaz, no por repositorio

    public CursoService(ModuloRepository moduloRepository,
                        LeccionRepository leccionRepository,
                        ProgresoLeccionRepository progresoRepository,
                        PreguntaExamenRepository preguntaRepository,
                        EnviosApi enviosApi) {
        this.moduloRepository = moduloRepository;
        this.leccionRepository = leccionRepository;
        this.progresoRepository = progresoRepository;
        this.preguntaRepository = preguntaRepository;
        this.enviosApi = enviosApi;
    }

    @Override
    public List<ModuloResumen> listarModulos(Long usuarioId) {
        return moduloRepository.findAllByOrderByOrden().stream()
                .map(m -> {
                    List<Leccion> lecciones = leccionRepository.findByModuloIdOrderByOrden(m.getId());
                    int total = lecciones.size();
                    int completadas = (int) lecciones.stream().filter(l -> estaCompletada(l, usuarioId)).count();
                    int progreso = total == 0 ? 0 : (completadas * 100) / total;
                    return new ModuloResumen(
                            m.getId(), m.getTitulo(), m.getDescripcion(), m.getIcono(),
                            total, completadas, progreso, progreso >= UMBRAL_EXAMEN);
                })
                .toList();
    }

    @Override
    public ModuloDetalle obtenerModulo(Long moduloId, Long usuarioId) {
        Modulo m = moduloRepository.findById(moduloId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Módulo no encontrado"));

        List<Leccion> lecciones = leccionRepository.findByModuloIdOrderByOrden(moduloId);
        List<LeccionVista> vistas = lecciones.stream()
                .map(l -> new LeccionVista(l.getId(), l.getTitulo(), l.getTipo(), l.getRetoId(),
                        estaCompletada(l, usuarioId), l.getOrden()))
                .toList();

        int total = lecciones.size();
        int completadas = (int) vistas.stream().filter(LeccionVista::completada).count();
        int progreso = total == 0 ? 0 : (completadas * 100) / total;

        return new ModuloDetalle(
                m.getId(), m.getTitulo(), m.getDescripcion(), m.getIcono(),
                vistas, total, completadas, progreso, UMBRAL_EXAMEN, progreso >= UMBRAL_EXAMEN);
    }

    @Override
    public LeccionDetalle obtenerLeccion(Long leccionId, Long usuarioId) {
        Leccion l = leccionRepository.findById(leccionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada"));
        Modulo m = moduloRepository.findById(l.getModuloId()).orElse(null);
        String tituloModulo = m != null ? m.getTitulo() : "";
        return new LeccionDetalle(l.getId(), l.getModuloId(), tituloModulo, l.getTitulo(), l.getTipo(),
                l.getContenido(), l.getEjemploCodigo(), l.getRetoId(), estaCompletada(l, usuarioId));
    }

    @Override
    public void completarLeccion(Long leccionId, Long usuarioId) {
        Leccion l = leccionRepository.findById(leccionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lección no encontrada"));
        if (l.getTipo() != TipoLeccion.TEORIA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Las lecciones de ejercicio se completan resolviendo el reto");
        }
        if (!progresoRepository.existsByUsuarioIdAndLeccionId(usuarioId, leccionId)) {
            progresoRepository.save(new ProgresoLeccion(usuarioId, leccionId));
        }
    }

    @Override
    public List<PreguntaVista> obtenerExamen(Long moduloId, Long usuarioId) {
        exigirExamenDesbloqueado(moduloId, usuarioId);
        return preguntaRepository.findByModuloIdOrderByOrden(moduloId).stream()
                .map(p -> new PreguntaVista(p.getId(), p.getEnunciado(),
                        List.of(p.getOpcionA(), p.getOpcionB(), p.getOpcionC(), p.getOpcionD())))
                .toList();
    }

    @Override
    public ResultadoExamen calificarExamen(Long moduloId, Long usuarioId, Map<Long, Integer> respuestas) {
        exigirExamenDesbloqueado(moduloId, usuarioId);
        List<PreguntaExamen> preguntas = preguntaRepository.findByModuloIdOrderByOrden(moduloId);
        if (preguntas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El módulo no tiene examen configurado");
        }

        int aciertos = 0;
        for (PreguntaExamen p : preguntas) {
            Integer elegida = respuestas.get(p.getId());
            if (elegida != null && elegida.equals(p.getCorrecta())) {
                aciertos++;
            }
        }

        int total = preguntas.size();
        int porcentaje = (aciertos * 100) / total;
        return new ResultadoExamen(aciertos, total, porcentaje, porcentaje >= UMBRAL_APROBACION);
    }

    // ---- helpers ----

    private void exigirExamenDesbloqueado(Long moduloId, Long usuarioId) {
        ModuloDetalle d = obtenerModulo(moduloId, usuarioId);
        if (!d.examenDesbloqueado()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Debes completar al menos " + UMBRAL_EXAMEN + "% de las lecciones para presentar el examen");
        }
    }

    private boolean estaCompletada(Leccion leccion, Long usuarioId) {
        if (usuarioId == null) return false;
        if (leccion.getTipo() == TipoLeccion.EJERCICIO) {
            return leccion.getRetoId() != null && enviosApi.tieneAceptado(usuarioId, leccion.getRetoId());
        }
        return progresoRepository.existsByUsuarioIdAndLeccionId(usuarioId, leccion.getId());
    }
}
