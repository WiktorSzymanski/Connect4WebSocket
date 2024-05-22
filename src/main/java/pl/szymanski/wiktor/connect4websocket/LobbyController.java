package pl.szymanski.wiktor.connect4websocket;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(("/lobby"))
@AllArgsConstructor
public class LobbyController {
    private final LobbyService lobbyService;

    @GetMapping()
    public ResponseEntity<List<Room>> getOpenRooms() {
        return ResponseEntity.ok(lobbyService.getOpenRooms());
    }

    @PostMapping()
    public ResponseEntity<UUID> createRoom(@RequestBody String name) {
        return ResponseEntity.ok(lobbyService.createRoom(name));
    }
}
