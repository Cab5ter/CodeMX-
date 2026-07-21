package mx.codemx.modules.cursos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de los módulos de aprendizaje. */
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    List<Modulo> findAllByOrderByOrdenAsc();
}
