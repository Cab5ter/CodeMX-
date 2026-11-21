package mx.codemx.modules.retos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos del módulo Retos. */
public interface RetoRepository extends JpaRepository<Reto, Long> {

    List<Reto> findAllByOrderByIdAsc();

    List<Reto> findByDificultad(Dificultad dificultad);

    Optional<Reto> findByTitulo(String titulo);
}
