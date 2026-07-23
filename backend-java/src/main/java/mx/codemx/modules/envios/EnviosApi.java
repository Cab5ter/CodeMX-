package mx.codemx.modules.envios;

import java.util.List;
import java.util.Optional;

public interface EnviosApi {

    Envio enviar(Envio envio);

    Optional<Envio> buscarPorId(long id);

    List<Envio> listarPorUsuario(long usuarioId);

    List<Envio> listarPorReto(long retoId);

    boolean tieneAceptado(long usuarioId, long retoId);
}
