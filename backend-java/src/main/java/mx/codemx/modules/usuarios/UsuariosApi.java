package mx.codemx.modules.usuarios;

import java.util.List;
import java.util.Optional;

public interface UsuariosApi {

    List<Usuario> listarTodos();

    Optional<Usuario> buscarPorId(long id);

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> autenticar(String email, String passwordHash);

    Usuario guardar(Usuario usuario);

    void eliminar(long id);
}
