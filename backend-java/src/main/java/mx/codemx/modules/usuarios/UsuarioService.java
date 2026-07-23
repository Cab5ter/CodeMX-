package mx.codemx.modules.usuarios;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService implements UsuariosApi {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(long id) {
        return repo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> autenticar(String email, String passwordHash) {
        return repo.findByEmail(email)
                .filter(u -> u.getPasswordHash().equals(passwordHash));
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return repo.save(usuario);
    }

    @Override
    public void eliminar(long id) {
        repo.deleteById(id);
    }
}
