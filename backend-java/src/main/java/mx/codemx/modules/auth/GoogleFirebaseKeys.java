package mx.codemx.modules.auth;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/** Certificados públicos de Google, compartidos por todas las cuentas Firebase. */
final class GoogleFirebaseKeys {
    private static final URI URL = URI.create(
            "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com");
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private Map<String, PublicKey> keys = Map.of();
    private Instant expires = Instant.EPOCH;
    private Instant lastFetch = Instant.EPOCH;

    synchronized PublicKey get(String kid) {
        Instant now = Instant.now();
        if (now.isAfter(expires) || (!keys.containsKey(kid) && now.isAfter(lastFetch.plusSeconds(60)))) {
            try {
                var response = http.send(HttpRequest.newBuilder(URL).timeout(Duration.ofSeconds(8))
                        .GET().build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) throw new IllegalStateException("Certificados no disponibles");
                Map<String, String> certificates = JsonMapper.builder().build()
                        .readValue(response.body(), new TypeReference<Map<String, String>>() { });
                Map<String, PublicKey> fresh = new HashMap<>();
                var factory = CertificateFactory.getInstance("X.509");
                for (var entry : certificates.entrySet()) {
                    var stream = new ByteArrayInputStream(entry.getValue().getBytes(StandardCharsets.UTF_8));
                    fresh.put(entry.getKey(), factory.generateCertificate(stream).getPublicKey());
                }
                long maxAge = 3600;
                String cacheControl = response.headers().firstValue("cache-control").orElse("");
                var match = java.util.regex.Pattern.compile("max-age=(\\d+)").matcher(cacheControl);
                if (match.find()) maxAge = Math.min(Long.parseLong(match.group(1)), 86400);
                keys = Map.copyOf(fresh);
                lastFetch = now;
                expires = now.plusSeconds(maxAge);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("No se pudieron obtener los certificados", e);
            } catch (Exception e) {
                throw new IllegalStateException("No se pudieron obtener los certificados", e);
            }
        }
        PublicKey key = keys.get(kid);
        if (key == null) throw new CredencialesInvalidasException();
        return key;
    }
}
