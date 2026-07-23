package mx.codemx.modules.duelos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DueloRepository extends JpaRepository<Duelo, Long> {

    @Query("select d from Duelo d where d.jugador1Id = :usuarioId or d.jugador2Id = :usuarioId "
            + "order by d.creadoEn desc")
    List<Duelo> listarPorUsuario(@Param("usuarioId") long usuarioId);
}
