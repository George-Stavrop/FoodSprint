package net.foodsprint.Exceptions.custom_exceptions;

public class StatusNotFoundException extends RuntimeException{

    public StatusNotFoundException(String message) {
        super(message);
    }
}
