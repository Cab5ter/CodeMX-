package mx.codemx.realtime;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DueloWebSocketHandler handler;
    private final JwtHandshakeInterceptor autenticacion;
    private final List<String> origenesPermitidos;

    public WebSocketConfig(DueloWebSocketHandler handler,
                           JwtHandshakeInterceptor autenticacion,
                           @Value("${codemx.cors.origenes:http://localhost:[*],http://127.0.0.1:[*]}")
                           List<String> origenesPermitidos) {
        this.handler = handler;
        this.autenticacion = autenticacion;
        this.origenesPermitidos = origenesPermitidos;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/api/hub/duelos")
                .addInterceptors(autenticacion)
                .setAllowedOriginPatterns(origenesPermitidos.toArray(String[]::new));
    }
}
