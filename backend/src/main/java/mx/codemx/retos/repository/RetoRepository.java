package mx.codemx.retos.repository;

import mx.codemx.retos.model.Dificultad;
import mx.codemx.retos.model.Reto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetoRepository extends JpaRepository<Reto, Long> {
    List<Reto> findByDificultad(Dificultad dificultad);
}
