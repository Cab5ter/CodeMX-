package mx.codemx.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import mx.codemx.modules.usuarios.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final int LONGITUD_MINIMA_SECRETO = 32;

    private static final String CLAIM_ID = "uid";
    private static final String CLAIM_NOMBRE = "nombre";

    private final SecretKey clave;
    private final long vigenciaMs;

    public JwtUtil(@Value("${codemx.jwt.secreto}") String secreto,
                   @Value("${codemx.jwt.vigencia-ms:86400000}") long vigenciaMs) {
        if (secreto == null || secreto.strip().length() < LONGITUD_MINIMA_SECRETO) {
            throw new IllegalStateException(
                    "codemx.jwt.secreto debe tener al menos " + LONGITUD_MINIMA_SECRETO
                            + " caracteres porque HS256 firma con una clave de 256 bits. "
                            + "Defínelo en application.yml o en la variable de entorno CODEMX_JWT_SECRETO.");
        }
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.vigenciaMs = vigenciaMs;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim(CLAIM_ID, usuario.getId())
                .claim(CLAIM_NOMBRE, usuario.getNombre())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(vigenciaMs)))
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }

    public Optional<UsuarioAutenticado> validar(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long id = claims.get(CLAIM_ID, Long.class);
            if (id == null || claims.getSubject() == null) {
                return Optional.empty();
            }
            return Optional.of(new UsuarioAutenticado(
                    id, claims.getSubject(), claims.get(CLAIM_NOMBRE, String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token rechazado: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> extraerCorreo(String token) {
        return validar(token).map(UsuarioAutenticado::email);
    }

    public long vigenciaEnSegundos() {
        return vigenciaMs / 1000;
    }
}
