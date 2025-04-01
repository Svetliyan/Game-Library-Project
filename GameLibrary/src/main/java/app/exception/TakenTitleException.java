package app.exception;

public class TakenTitleException extends RuntimeException{
    public TakenTitleException(String message) {
        super(message);
    }

    public TakenTitleException() {
    }
}
