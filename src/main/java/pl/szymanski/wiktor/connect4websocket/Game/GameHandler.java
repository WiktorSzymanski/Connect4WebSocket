package pl.szymanski.wiktor.connect4websocket.Game;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;
import pl.szymanski.wiktor.connect4websocket.exceptions.ColumnFullException;
import pl.szymanski.wiktor.connect4websocket.exceptions.NotYourMoveException;
import pl.szymanski.wiktor.connect4websocket.lobby.LobbyService;
import pl.szymanski.wiktor.connect4websocket.exceptions.InvalidRoomIdentifiedException;
import pl.szymanski.wiktor.connect4websocket.lobby.Room;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class GameHandler implements WebSocketHandler {
    private final LobbyService lobbyService;

    private final Map<WebSocketSession, Room> rooms = new HashMap<>();

    public GameHandler(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

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

        rooms.put(session, lobbyService.getRoomById(roomId));
        log.info("Connection established with room: {}", roomId);
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        int col = Integer.parseInt((String) message.getPayload());
        log.info("Message: {}", col);
        Room room = rooms.get(session);

        int resp;
        try {
            resp = room.getGameState().placeToken(col, room.getSessions().indexOf(session));
        } catch (NotYourMoveException e) {
            session.sendMessage(new TextMessage("9"));
            log.error("Error:", e);
            return;
        } catch (ColumnFullException e) {
            session.sendMessage(new TextMessage("10"));
            log.error("Error:", e);
            return;
        }

        room.getSessions().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(Integer.toString(col)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        if (resp != -1) {
            int response = 11 + resp;
            room.getSessions().forEach(s -> {
                try {
                    s.sendMessage(new TextMessage(Integer.toString(response)));
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
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
