package net.foodsprint.service;

import lombok.RequiredArgsConstructor;
import net.foodsprint.Exceptions.custom_exceptions.CategoryNotFoundException;
import net.foodsprint.domain.Category;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final RestaurantService restaurantService;

    private final CategoryRepository categoryRepository;


    @Override
    public Category createCategory(String name, Long userId) {
        Restaurant restaurant = restaurantService.getRestaurantByUserId(userId);

        Category category = new Category();
        category.setName(name);
        category.setRestaurant(restaurant);

        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findCategoryByRestaurantId(Long id)  {
        Restaurant restaurant = restaurantService.findRestaurantById(id);

        return categoryRepository.findByRestaurantId(id);
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Η κατηγορία δεν υπάρχει"));
    }
}
