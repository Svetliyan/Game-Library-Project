package app.exception;

public class GameAlreadyExistException extends RuntimeException{
    public GameAlreadyExistException(String message) {
        super(message);
    }

    public GameAlreadyExistException() {
    }
}
