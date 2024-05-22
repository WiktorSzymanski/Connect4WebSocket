package pl.szymanski.wiktor.connect4websocket.exceptions;

public class InvalidRoomIdentifiedException extends RuntimeException {
    public InvalidRoomIdentifiedException() {
        super("Invalid room identified");
    }
}
