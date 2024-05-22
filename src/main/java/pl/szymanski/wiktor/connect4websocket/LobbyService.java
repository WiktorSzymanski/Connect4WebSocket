package pl.szymanski.wiktor.connect4websocket;


import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import pl.szymanski.wiktor.connect4websocket.handler.RoomNotFoundException;

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

    public UUID createRoom(String name) {
        Room room = new Room(UUID.randomUUID(), name, true, new ArrayList<>());
        rooms.add(room);
        return room.getId();
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
                    }
                    return room.getId();
                })
                .orElseThrow(RoomNotFoundException::new);

    }
}
