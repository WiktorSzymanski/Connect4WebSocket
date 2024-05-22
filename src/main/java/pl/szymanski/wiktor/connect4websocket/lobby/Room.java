package pl.szymanski.wiktor.connect4websocket.lobby;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Room {
    private UUID id;
    private String name;
    private Boolean open;
    @JsonIgnore
    private List<WebSocketSession> sessions;
}
