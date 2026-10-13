package net.foodsprint.service;

import net.foodsprint.domain.IngredientCategory;
import net.foodsprint.domain.IngredientsItem;

import java.util.List;

public interface IngredientsService {

    IngredientCategory createIngredientCategory(String name, Long restaurantId);

     IngredientCategory findIngredientCategoryById(Long id);

     List<IngredientCategory> findIngredientCategoryByRestaurantId(Long id);

     IngredientsItem createIngredientItem(Long restaurantId, String ingredientName, Long category);

     List<IngredientsItem> findRestaurantsIngredients(Long restaurantId);

     IngredientsItem updateStock(Long id);


}
