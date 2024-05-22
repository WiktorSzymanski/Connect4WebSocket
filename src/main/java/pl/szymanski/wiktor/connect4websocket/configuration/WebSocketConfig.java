package pl.szymanski.wiktor.connect4websocket.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import pl.szymanski.wiktor.connect4websocket.LobbyService;
import pl.szymanski.wiktor.connect4websocket.handler.GameHandler;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final LobbyService lobbyService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler(), "/game/{id}").setAllowedOrigins("*");
    }

    @Bean
    WebSocketHandler gameHandler() {
        return new GameHandler(lobbyService);
    }
}
