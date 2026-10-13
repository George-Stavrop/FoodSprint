package net.foodsprint.service;

import net.foodsprint.domain.Food;
import net.foodsprint.web.dto.request.CreateFoodRequest;

import java.util.List;

public interface FoodService {

     Food createFood(CreateFoodRequest req);

    void deleteFood(Long foodId);

     List<Food> getRestaurantsFood(Long restaurantId,
                                          String foodCategory);
     Food findFoodById(Long id);

     Food updateAvailabilityStatus(Long foodId);
}
