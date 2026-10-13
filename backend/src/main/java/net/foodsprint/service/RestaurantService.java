package net.foodsprint.service;

import net.foodsprint.domain.Restaurant;
import net.foodsprint.domain.User;
import net.foodsprint.web.dto.request.CreateRestaurantRequest;

import java.util.List;

public interface RestaurantService {

     Restaurant createRestaurant(CreateRestaurantRequest req, User user);

     Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant);

     void deleteRestaurant(Long restaurantId);

     List<Restaurant> getAllRestaurants();

     Restaurant findRestaurantById(Long id) ;

     Restaurant getRestaurantByUserId(Long userId);

     Restaurant updateRestaurantStatus(Long id);


}
