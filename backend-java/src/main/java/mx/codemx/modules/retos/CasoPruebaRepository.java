package mx.codemx.modules.retos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de los casos de prueba de un reto. */
public interface CasoPruebaRepository extends JpaRepository<CasoPrueba, Long> {

    List<CasoPrueba> findByRetoIdOrderByIdAsc(long retoId);
}
