package mx.codemx.usuarios.api;

import mx.codemx.usuarios.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * API pública del módulo Usuarios.
 *
 * Es el único punto de entrada que otros módulos (o el gateway) pueden usar para
 * interactuar con este módulo. Oculta el repositorio y la implementación internos.
 * Según el ADR-03, Usuarios es independiente: sólo lo consume el gateway.
 */
public interface UsuariosApi {

    List<Usuario> listarTodos();

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    Usuario guardar(Usuario usuario);

    void eliminar(Long id);
}
