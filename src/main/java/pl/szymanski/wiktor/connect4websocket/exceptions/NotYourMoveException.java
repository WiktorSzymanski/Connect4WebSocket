package pl.szymanski.wiktor.connect4websocket.exceptions;

public class NotYourMoveException extends RuntimeException {
    public NotYourMoveException() { super("Not your time to move!"); }
}
