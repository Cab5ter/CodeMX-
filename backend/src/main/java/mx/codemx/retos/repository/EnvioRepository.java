package mx.codemx.retos.repository;

import mx.codemx.retos.model.Envio;
import mx.codemx.retos.model.Veredicto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    List<Envio> findByUsuarioId(Long usuarioId);
    List<Envio> findByRetoId(Long retoId);
    boolean existsByUsuarioIdAndRetoIdAndVeredicto(Long usuarioId, Long retoId, Veredicto veredicto);
}
