package mx.codemx.modules.usuarios;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos del módulo Usuarios. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
}
