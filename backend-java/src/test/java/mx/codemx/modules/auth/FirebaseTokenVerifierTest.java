package mx.codemx.modules.auth;

import io.jsonwebtoken.Jwts;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FirebaseTokenVerifierTest {
    private KeyPair key;
    private FirebaseTokenVerifier verifier;

    @BeforeEach
    void setUp() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        key = generator.generateKeyPair();
        verifier = new FirebaseTokenVerifier("codemx-test", kid -> key.getPublic());
    }

    private String token(String project, Instant expiration, String email) {
        Instant now = Instant.now();
        return Jwts.builder().header().keyId("test-key").and()
                .issuer("https://securetoken.google.com/" + project)
                .audience().add(project).and().subject("firebase-uid")
                .issuedAt(Date.from(now)).expiration(Date.from(expiration))
                .claim("auth_time", now.getEpochSecond()).claim("email", email)
                .claim("email_verified", true).claim("name", "Estudiante")
                .signWith(key.getPrivate(), Jwts.SIG.RS256).compact();
    }

    @Test void acceptsSignedTokenFromConfiguredProject() {
        var identity = verifier.verify(token("codemx-test", Instant.now().plusSeconds(3600), "test@tecdesoftware.edu.mx"));
        assertEquals("firebase-uid", identity.uid());
        assertEquals("test@tecdesoftware.edu.mx", identity.email());
        assertTrue(identity.emailVerified());
    }
    @Test void rejectsOtherProject() {
        assertThrows(CredencialesInvalidasException.class, () -> verifier.verify(
                token("otro-proyecto", Instant.now().plusSeconds(3600), "test@tecdesoftware.edu.mx")));
    }
    @Test void rejectsExpiredToken() {
        assertThrows(CredencialesInvalidasException.class, () -> verifier.verify(
                token("codemx-test", Instant.now().minusSeconds(120), "test@tecdesoftware.edu.mx")));
    }
    @Test void rejectsInvalidSignature() throws Exception {
        var other = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        var wrongKeyVerifier = new FirebaseTokenVerifier("codemx-test", kid -> other.getPublic());
        assertThrows(CredencialesInvalidasException.class, () -> wrongKeyVerifier.verify(
                token("codemx-test", Instant.now().plusSeconds(3600), "test@tecdesoftware.edu.mx")));
    }
    @Test void rejectsMissingEmailAndMalformedTokens() {
        assertThrows(CredencialesInvalidasException.class, () -> verifier.verify(
                token("codemx-test", Instant.now().plusSeconds(3600), null)));
        assertThrows(CredencialesInvalidasException.class, () -> verifier.verify("not-a-token"));
        assertThrows(CredencialesInvalidasException.class, () -> verifier.verify(Jwts.builder()
                .subject("firebase-uid").compact()));
    }
    @Test void rejectsMissingExpiry() {
        var jwt = Jwts.builder().header().keyId("test-key").and()
                .issuer("https://securetoken.google.com/codemx-test").audience().add("codemx-test").and()
                .subject("firebase-uid").issuedAt(new Date()).claim("auth_time", Instant.now().getEpochSecond())
                .claim("email", "test@tecdesoftware.edu.mx").signWith(key.getPrivate(), Jwts.SIG.RS256).compact();
        assertThrows(CredencialesInvalidasException.class, () -> verifier.verify(jwt));
    }
}
