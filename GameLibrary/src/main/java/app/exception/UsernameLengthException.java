package app.exception;

public class UsernameLengthException extends RuntimeException{
    public UsernameLengthException(String message) {
        super(message);
    }

    public UsernameLengthException() {
    }
}
