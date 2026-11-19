package mx.codemx.modules.usuarios;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService implements UsuariosApi {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
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
    public Optional<Usuario> autenticar(String email, String contrasena) {
        return repo.findByEmail(email)
                .filter(u -> encoder.matches(contrasena, u.getPasswordHash()));
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        if (!yaEsHashBcrypt(usuario.getPasswordHash())) {
            usuario.setPasswordHash(encoder.encode(usuario.getPasswordHash()));
        }
        return repo.save(usuario);
    }

    static boolean yaEsHashBcrypt(String valor) {
        return valor != null && valor.length() == 60 && valor.startsWith("$2");
    }

    @Override
    public Usuario buscarOCrearDesdeFirebase(String uid, String email, String nombre, boolean correoVerificado) {
        var existente = repo.findByFirebaseUid(uid);
        if (existente.isPresent()) return existente.get();
        String correo = email.strip().toLowerCase(java.util.Locale.ROOT);
        var porCorreo = repo.findByEmailIgnoreCase(correo);
        Usuario usuario;
        if (porCorreo.isPresent()) {
            usuario = porCorreo.get();
            // Una cuenta Firebase sin correo verificado no puede apropiarse de una cuenta web.
            if (!correoVerificado || usuario.getFirebaseUid() != null) {
                throw new mx.codemx.modules.auth.CredencialesInvalidasException();
            }
        } else {
            usuario = new Usuario();
            usuario.setEmail(correo);
            usuario.setNombre(nombre == null || nombre.isBlank() ? correo.split("@")[0] : nombre);
            // Firebase conserva la contraseña. El acceso local queda protegido por un secreto aleatorio.
            usuario.setPasswordHash(encoder.encode(java.util.UUID.randomUUID().toString()));
        }
        usuario.setFirebaseUid(uid);
        return repo.save(usuario);
    }

    @Override
    public void eliminar(long id) {
        repo.deleteById(id);
    }
}
