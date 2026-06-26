package mx.codemx.cursos.repository;

import mx.codemx.cursos.model.PreguntaExamen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaExamenRepository extends JpaRepository<PreguntaExamen, Long> {
    List<PreguntaExamen> findByModuloIdOrderByOrden(Long moduloId);
}
