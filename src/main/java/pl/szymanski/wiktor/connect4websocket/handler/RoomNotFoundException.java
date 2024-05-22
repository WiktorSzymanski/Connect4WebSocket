package pl.szymanski.wiktor.connect4websocket.handler;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException() {
        super("Room full or non existing");
    }
}
