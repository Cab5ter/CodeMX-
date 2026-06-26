package mx.codemx.cursos.repository;

import mx.codemx.cursos.model.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeccionRepository extends JpaRepository<Leccion, Long> {
    List<Leccion> findByModuloIdOrderByOrden(Long moduloId);
    long countByModuloId(Long moduloId);
}
