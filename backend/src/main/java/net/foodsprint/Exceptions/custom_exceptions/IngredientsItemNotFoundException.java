package net.foodsprint.Exceptions.custom_exceptions;

public class IngredientsItemNotFoundException extends RuntimeException{

    public IngredientsItemNotFoundException(String message) {
        super(message);
    }
}
