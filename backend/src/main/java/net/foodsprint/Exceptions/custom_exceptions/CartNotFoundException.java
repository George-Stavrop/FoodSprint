package net.foodsprint.Exceptions.custom_exceptions;

public class CartNotFoundException extends RuntimeException{

    public CartNotFoundException(String message){
        super(message);
    }
}