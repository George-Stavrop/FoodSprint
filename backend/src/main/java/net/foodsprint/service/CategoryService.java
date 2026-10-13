package net.foodsprint.service;

import net.foodsprint.domain.Category;

import java.util.List;

public interface CategoryService {

     Category createCategory(String name, Long userId);

     List<Category> findCategoryByRestaurantId(Long id);

     Category findCategoryById(Long id);
}
