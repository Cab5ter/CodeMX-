package mx.codemx.realtime;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Registra el canal de tiempo real del modo 1 vs 1 en /api/hub/duelos, la ruta que el
 * proxy de Vite ya redirige con {@code ws: true}.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DueloWebSocketHandler handler;

    public WebSocketConfig(DueloWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/api/hub/duelos").setAllowedOriginPatterns("*");
    }
}
