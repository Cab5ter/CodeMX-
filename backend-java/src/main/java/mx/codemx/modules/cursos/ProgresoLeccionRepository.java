package mx.codemx.modules.cursos;

import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos del avance en lecciones de teoría. */
public interface ProgresoLeccionRepository extends JpaRepository<ProgresoLeccion, Long> {

    boolean existsByUsuarioIdAndLeccionId(long usuarioId, long leccionId);
}
