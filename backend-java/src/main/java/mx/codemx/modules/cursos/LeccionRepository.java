package mx.codemx.modules.cursos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeccionRepository extends JpaRepository<Leccion, Long> {

    List<Leccion> findByModuloIdOrderByOrdenAsc(long moduloId);

    boolean existsByRetoId(long retoId);
}
