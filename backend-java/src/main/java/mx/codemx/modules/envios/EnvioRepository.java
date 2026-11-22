package mx.codemx.modules.envios;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

    List<Envio> findByUsuarioId(long usuarioId);

    List<Envio> findByRetoId(long retoId);

    boolean existsByUsuarioIdAndRetoIdAndVeredicto(long usuarioId, long retoId, Veredicto veredicto);
}
