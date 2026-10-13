package net.foodsprint.Exceptions.custom_exceptions;

public class CartItemNotFoundException extends RuntimeException{

    public CartItemNotFoundException(String message){
        super(message);
    }
}