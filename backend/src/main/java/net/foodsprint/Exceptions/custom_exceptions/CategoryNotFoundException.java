package net.foodsprint.Exceptions.custom_exceptions;

public class CategoryNotFoundException extends RuntimeException{

    public CategoryNotFoundException(String message){
        super(message);
    }

    public CategoryNotFoundException(Long id){
        super("Η κατηγορία " + id + "δεν υπάρχει");
    }
}
