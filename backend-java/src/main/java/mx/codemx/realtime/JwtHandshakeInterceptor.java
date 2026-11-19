package mx.codemx.realtime;

import java.util.Map;
import mx.codemx.security.JwtUtil;
import mx.codemx.security.UsuarioAutenticado;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    public static final String ATRIBUTO_USUARIO = "usuarioAutenticado";

    private final JwtUtil jwt;

    public JwtHandshakeInterceptor(JwtUtil jwt) {
        this.jwt = jwt;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler handler, Map<String, Object> atributos) {
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");

        UsuarioAutenticado usuario = jwt.validar(token).orElse(null);
        if (usuario == null) {
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }

        atributos.put(ATRIBUTO_USUARIO, usuario);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler handler, Exception excepcion) {
    }
}
