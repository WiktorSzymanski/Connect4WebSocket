package pl.szymanski.wiktor.connect4websocket.handler;

import java.security.InvalidKeyException;

public class InvalidRoomIdentifiedException extends RuntimeException {
    public InvalidRoomIdentifiedException() {
        super("Invalid room identified");
    }
}
