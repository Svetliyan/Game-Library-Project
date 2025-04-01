package app.exception;

public class NotEnoughMoney extends RuntimeException{
    public NotEnoughMoney(String message) {
        super(message);
    }

    public NotEnoughMoney() {
    }
}
