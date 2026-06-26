package mx.codemx.cursos.repository;

import mx.codemx.cursos.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    List<Modulo> findAllByOrderByOrden();
}
