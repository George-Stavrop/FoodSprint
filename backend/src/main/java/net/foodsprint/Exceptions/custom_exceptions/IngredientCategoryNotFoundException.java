package net.foodsprint.Exceptions.custom_exceptions;

public class IngredientCategoryNotFoundException extends RuntimeException{

    public IngredientCategoryNotFoundException(String message) {
        super(message);
    }
}
