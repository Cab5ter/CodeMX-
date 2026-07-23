package mx.codemx.modules.cursos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgresoLeccionRepository extends JpaRepository<ProgresoLeccion, Long> {

    boolean existsByUsuarioIdAndLeccionId(long usuarioId, long leccionId);
}
