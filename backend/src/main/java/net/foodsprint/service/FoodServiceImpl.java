package net.foodsprint.service;

import lombok.RequiredArgsConstructor;
import net.foodsprint.Exceptions.custom_exceptions.CategoryNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.FoodNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.RestaurantNotFoundException;
import net.foodsprint.web.dto.request.CreateFoodRequest;
import net.foodsprint.domain.Category;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.IngredientsItem;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.repository.CategoryRepository;
import net.foodsprint.repository.FoodRepository;
import net.foodsprint.repository.IngredientsItemRepository;
import net.foodsprint.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {


    private final FoodRepository foodRepository;

    private final CategoryRepository categoryRepository;

    private final RestaurantRepository restaurantRepository;

    private final IngredientsItemRepository ingredientsItemRepository;

    @Override
    public Food createFood(CreateFoodRequest req) {

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(req.getCategoryId()));

        Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(req.getRestaurantId()));

        List<IngredientsItem> ingredients = ingredientsItemRepository.findByRestaurantId(restaurant.getId());

        Food food = new Food();
        food.setFoodCategory(category);
        food.setRestaurant(restaurant);
        food.setDescription(req.getDescription());
        food.setImages(req.getImages());
        food.setName(req.getName());
        food.setPrice(req.getPrice());
        food.setIngredients(ingredients);
        food.setCreationDate(LocalDateTime.now());

        Food savedFood = foodRepository.save(food);
        restaurant.getFoods().add(savedFood);

        return savedFood;
    }

    @Override
    public void deleteFood(Long foodId) {
        Food food = findFoodById(foodId);
        food.setRestaurant(null);
        foodRepository.save(food);
    }

    @Override
    public List<Food> getRestaurantsFood(Long restaurantId,
                                         String foodCategory) {

        List<Food> foods = foodRepository.findByRestaurantId(restaurantId);


        if (foodCategory != null && !foodCategory.equals("")) {
            foods = filterByCategory(foods, foodCategory);
        }
        return foods;
    }

    private List<Food> filterByCategory(List<Food> foods, String foodCategory) {
        return foods.stream().filter(food -> {
            if (food.getFoodCategory() != null) {
                return food.getFoodCategory().getName().equals(foodCategory);
            }
            return false;
        }).collect(Collectors.toList());
    }




    @Override
    public Food findFoodById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new FoodNotFoundException("Το προιόν δεν υπάρχει"));

    }

    @Override
    public Food updateAvailabilityStatus(Long foodId) {

        Food food = findFoodById(foodId);
        food.setAvailable(!food.isAvailable());
        return foodRepository.save(food);

    }
}
