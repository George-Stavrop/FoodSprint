package net.foodsprint.Exceptions.custom_exceptions;

public class FoodNotFoundException extends RuntimeException{

    public FoodNotFoundException(String message) {
        super(message);
    }
}
