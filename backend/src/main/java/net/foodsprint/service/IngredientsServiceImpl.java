package net.foodsprint.service;

import lombok.RequiredArgsConstructor;
import net.foodsprint.Exceptions.custom_exceptions.IngredientCategoryNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.IngredientsItemNotFoundException;
import net.foodsprint.domain.IngredientCategory;
import net.foodsprint.domain.IngredientsItem;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.repository.IngredientCategoryRepository;
import net.foodsprint.repository.IngredientsItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientsServiceImpl implements IngredientsService {

    private final IngredientsItemRepository ingredientsItemRepository;

    private final IngredientCategoryRepository ingredientCategoryRepository;

    private final RestaurantService restaurantService;


    @Override
    public IngredientCategory createIngredientCategory(String name, Long restaurantId) {

        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        IngredientCategory category = new IngredientCategory();
        category.setRestaurant(restaurant);
        category.setName(name);

        return ingredientCategoryRepository.save(category);
    }

    @Override
    public IngredientCategory findIngredientCategoryById(Long id) {
        return ingredientCategoryRepository.findById(id)
                .orElseThrow(() -> new IngredientCategoryNotFoundException("Το υλικό δεν υπάρχει"));
    }

    @Override
    public List<IngredientCategory> findIngredientCategoryByRestaurantId(Long id) {
        restaurantService.findRestaurantById(id);

        return ingredientCategoryRepository.findByRestaurantId(id);
    }

    @Override
    public IngredientsItem createIngredientItem(Long restaurantId, String ingredientName, Long categoryId) {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        IngredientCategory category = findIngredientCategoryById(categoryId);

        IngredientsItem item = new IngredientsItem();
        item.setName(ingredientName);
        item.setRestaurant(restaurant);
        item.setCategory(category);

        IngredientsItem ingredient = ingredientsItemRepository.save(item);
        category.getIngredients().add(ingredient);

        return ingredient;
    }

    @Override
    public List<IngredientsItem> findRestaurantsIngredients(Long restaurantId) {

        return ingredientsItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public IngredientsItem updateStock(Long id) {

        IngredientsItem ingredientsItem = ingredientsItemRepository.findById(id)
                .orElseThrow(() -> new IngredientsItemNotFoundException("Το υλικό δεν βρέθηκε"));

        ingredientsItem.setInStock(!ingredientsItem.isInStock());
        return ingredientsItemRepository.save(ingredientsItem);
    }
}
