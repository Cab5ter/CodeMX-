package mx.codemx.persistence;

import java.util.List;
import mx.codemx.modules.usuarios.Usuario;
import mx.codemx.modules.usuarios.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class MigracionContrasenas implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MigracionContrasenas.class);

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public MigracionContrasenas(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Usuario> pendientes = usuarios.findAll().stream()
                .filter(u -> !esHashBcrypt(u.getPasswordHash()))
                .toList();

        if (pendientes.isEmpty()) {
            return;
        }

        for (Usuario usuario : pendientes) {
            usuario.setPasswordHash(encoder.encode(usuario.getPasswordHash()));
        }
        usuarios.saveAll(pendientes);

        log.info("Se cifraron con BCrypt {} contraseñas que estaban en texto plano. "
                + "Los usuarios afectados entran con la misma contraseña de antes.", pendientes.size());
    }

    private static boolean esHashBcrypt(String valor) {
        return valor != null && valor.length() == 60 && valor.startsWith("$2");
    }
}
