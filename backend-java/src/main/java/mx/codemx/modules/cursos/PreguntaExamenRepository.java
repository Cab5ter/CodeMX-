package mx.codemx.modules.cursos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreguntaExamenRepository extends JpaRepository<PreguntaExamen, Long> {

    List<PreguntaExamen> findByModuloIdOrderByOrdenAsc(long moduloId);
}
