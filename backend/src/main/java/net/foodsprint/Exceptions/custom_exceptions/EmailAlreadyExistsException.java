package net.foodsprint.Exceptions.custom_exceptions;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String email) {
        super("Το email " + email + " χρησιμοποιείται ήδη.");
    }
}
