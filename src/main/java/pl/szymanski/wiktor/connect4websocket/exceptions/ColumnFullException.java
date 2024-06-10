package pl.szymanski.wiktor.connect4websocket.exceptions;

public class ColumnFullException extends RuntimeException {
    public ColumnFullException() { super("Column is full!"); }
}
