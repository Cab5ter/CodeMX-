package mx.codemx.modules.usuarios;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Usuarios. Único punto de entrada para el gateway.
 * Según el ADR-03/04, Usuarios es independiente: sólo lo consume el gateway.
 */
public interface UsuariosApi {

    List<Usuario> listarTodos();

    Optional<Usuario> buscarPorId(long id);

    Optional<Usuario> buscarPorEmail(String email);

    /** Valida credenciales (correo + contraseña). Devuelve vacío si no coinciden. */
    Optional<Usuario> autenticar(String email, String passwordHash);

    Usuario guardar(Usuario usuario);

    void eliminar(long id);
}
