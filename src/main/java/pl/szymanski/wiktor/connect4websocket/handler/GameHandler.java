package pl.szymanski.wiktor.connect4websocket.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;
import pl.szymanski.wiktor.connect4websocket.lobby.LobbyService;
import pl.szymanski.wiktor.connect4websocket.exceptions.InvalidRoomIdentifiedException;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
public class GameHandler implements WebSocketHandler {
    private final LobbyService lobbyService;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        UUID roomId = Stream.of(session)
                .map(WebSocketSession::getUri)
                .filter(Objects::nonNull)
                .map(URI::getPath)
                .map(path -> path.split("/")[2])
                .map(UUID::fromString)
                .map(id -> lobbyService.joinRoom(id, session))
                .findAny()
                .orElseThrow(InvalidRoomIdentifiedException::new);

        log.info("Connection established with room: {}", roomId);
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String tutorial = (String) message.getPayload();
        log.info("Message: {}", tutorial);
        List<WebSocketSession> sessions = lobbyService.getSessions(
                Stream.of(session)
                        .map(WebSocketSession::getUri)
                        .filter(Objects::nonNull)
                        .map(URI::getPath)
                        .map(path -> path.split("/")[2])
                        .map(UUID::fromString)
                        .findAny()
                        .orElseThrow(InvalidRoomIdentifiedException::new));

        sessions.forEach(s -> {
            try {
                s.sendMessage(new TextMessage("Someone sent: " + tutorial));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Exception occurred: {} on session: {}", exception.getMessage(), session.getId());

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
