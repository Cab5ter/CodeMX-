package mx.codemx.modules.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import java.security.Key;
import java.security.PublicKey;
import java.time.Instant;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Verifica ID tokens de Firebase; nunca confía en claims enviados sin firma. */
@Component
public class FirebaseTokenVerifier {
    private final String projectId;
    private final Function<String, PublicKey> keys;

    @org.springframework.beans.factory.annotation.Autowired
    public FirebaseTokenVerifier(@Value("${codemx.firebase.project-id:}") String projectId) {
        this(projectId, new GoogleFirebaseKeys()::get);
    }

    FirebaseTokenVerifier(String projectId, Function<String, PublicKey> keys) {
        this.projectId = projectId;
        this.keys = keys;
    }

    public Identity verify(String token) {
        if (projectId.isBlank()) {
            throw new IllegalStateException("Configura CODEMX_FIREBASE_PROJECT_ID en el backend.");
        }
        try {
            Claims claims = Jwts.parser()
                    .keyLocator(new LocatorAdapter<Key>() {
                        @Override
                        public Key locate(ProtectedHeader header) {
                            if (!"RS256".equals(header.getAlgorithm()) || header.getKeyId() == null) {
                                throw new CredencialesInvalidasException();
                            }
                            return keys.apply(header.getKeyId());
                        }
                    })
                    .requireIssuer("https://securetoken.google.com/" + projectId)
                    .requireAudience(projectId)
                    .clockSkewSeconds(30)
                    .build().parseSignedClaims(token).getPayload();
            String uid = claims.getSubject();
            String email = claims.get("email", String.class);
            Number authTime = claims.get("auth_time", Number.class);
            Instant limit = Instant.now().plusSeconds(30);
            if (uid == null || uid.isBlank() || uid.length() > 128
                    || email == null || email.isBlank() || claims.getExpiration() == null
                    || claims.getIssuedAt() == null || claims.getIssuedAt().toInstant().isAfter(limit)
                    || authTime == null || authTime.longValue() > limit.getEpochSecond()) {
                throw new CredencialesInvalidasException();
            }
            return new Identity(uid, email, claims.get("name", String.class),
                    Boolean.TRUE.equals(claims.get("email_verified", Boolean.class)));
        } catch (RuntimeException error) {
            throw new CredencialesInvalidasException();
        }
    }

    public record Identity(String uid, String email, String name, boolean emailVerified) { }
}
