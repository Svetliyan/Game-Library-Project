package app.exception;

public class CategoryAlreadyExistException extends RuntimeException{
    public CategoryAlreadyExistException(String message) {
        super(message);
    }

    public CategoryAlreadyExistException() {
    }
}
