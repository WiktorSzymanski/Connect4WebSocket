package pl.szymanski.wiktor.connect4websocket.lobby;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import pl.szymanski.wiktor.connect4websocket.Game.GameState;
import pl.szymanski.wiktor.connect4websocket.exceptions.RoomNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LobbyService {
    private final List<Room> rooms = new ArrayList<>();

    public List<Room> getOpenRooms() {
        return rooms.stream()
                .filter(Room::getOpen)
                .collect(Collectors.toList());
    }

    public List<UUID> getRoomIds() {
        return rooms.stream()
                .map(Room::getId)
                .collect(Collectors.toList());
    }

    public Room getRoomById(UUID roomId) {
        return rooms.stream()
                .filter(room -> room.getId().equals(roomId))
                .findFirst()
                .orElseThrow(RoomNotFoundException::new);
    }

    public Room createRoom(String name) {
        Room room = new Room(UUID.randomUUID(), name.substring(1, name.length() - 1), true, new ArrayList<>(), new GameState());
        rooms.add(room);
        return room;
    }

    public UUID joinRoom(UUID id, WebSocketSession session) {
        return rooms.stream()
                .filter(Room::getOpen)
                .filter(room -> room.getId().equals(id))
                .findFirst()
                .map(room -> {
                    room.getSessions().add(session);
                    if (room.getSessions().size() == 2) {
                        room.setOpen(false);
                        int a = 0;
                        for (WebSocketSession s : room.getSessions()) {
                            try {
                                s.sendMessage(new TextMessage(String.valueOf(7 + a)));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            a++;
                        }
                    }
                    return room.getId();
                })
                .orElseThrow(RoomNotFoundException::new);
    }

    public List<WebSocketSession> getSessions(UUID id) {
        return rooms.stream()
                .filter(room -> room.getId().equals(id))
                .findFirst()
                .map(Room::getSessions)
                .orElseThrow(RoomNotFoundException::new);
    }
}
