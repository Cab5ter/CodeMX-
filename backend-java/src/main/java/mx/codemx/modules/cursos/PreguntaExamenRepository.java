package mx.codemx.modules.cursos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de las preguntas de examen. */
public interface PreguntaExamenRepository extends JpaRepository<PreguntaExamen, Long> {

    List<PreguntaExamen> findByModuloIdOrderByOrdenAsc(long moduloId);
}
