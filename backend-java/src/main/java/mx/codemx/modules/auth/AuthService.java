package mx.codemx.modules.auth;

import mx.codemx.modules.usuarios.Usuario;
import mx.codemx.modules.usuarios.UsuariosApi;
import mx.codemx.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService implements AuthApi {

    private final UsuariosApi usuarios;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthService(UsuariosApi usuarios, PasswordEncoder encoder, JwtUtil jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Override
    public LoginResponse iniciarSesion(LoginRequest credenciales) {
        Usuario usuario = usuarios.buscarPorEmail(credenciales.email())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!encoder.matches(credenciales.contrasena(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        return LoginResponse.de(
                jwt.generarToken(usuario),
                jwt.vigenciaEnSegundos(),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail());
    }
}
