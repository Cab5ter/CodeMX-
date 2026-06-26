package mx.codemx.cursos.repository;

import mx.codemx.cursos.model.ProgresoLeccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgresoLeccionRepository extends JpaRepository<ProgresoLeccion, Long> {
    boolean existsByUsuarioIdAndLeccionId(Long usuarioId, Long leccionId);
    List<ProgresoLeccion> findByUsuarioId(Long usuarioId);
}
