package net.foodsprint.Exceptions.custom_exceptions;

public class RestaurantNotFoundException extends RuntimeException{

    public RestaurantNotFoundException(Long id) {
        super("Το εστιατόριο με " + id + "δεν βρέθηκε");
    }
}
