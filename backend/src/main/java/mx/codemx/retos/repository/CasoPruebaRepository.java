package mx.codemx.retos.repository;

import mx.codemx.retos.model.CasoPrueba;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CasoPruebaRepository extends JpaRepository<CasoPrueba, Long> {
    List<CasoPrueba> findByRetoId(Long retoId);
}
