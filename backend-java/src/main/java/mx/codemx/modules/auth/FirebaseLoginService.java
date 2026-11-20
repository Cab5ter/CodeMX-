package mx.codemx.modules.auth;

import mx.codemx.modules.usuarios.UsuariosApi;
import mx.codemx.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FirebaseLoginService {
    private final FirebaseTokenVerifier verifier;
    private final UsuariosApi usuarios;
    private final JwtUtil jwt;

    public FirebaseLoginService(FirebaseTokenVerifier verifier, UsuariosApi usuarios, JwtUtil jwt) {
        this.verifier = verifier;
        this.usuarios = usuarios;
        this.jwt = jwt;
    }

    @Transactional
    public LoginResponse login(String idToken) {
        var identity = verifier.verify(idToken);
        var usuario = usuarios.buscarOCrearDesdeFirebase(identity.uid(), identity.email(),
                identity.name(), identity.emailVerified());
        return LoginResponse.de(jwt.generarToken(usuario), jwt.vigenciaEnSegundos(),
                usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }
}
