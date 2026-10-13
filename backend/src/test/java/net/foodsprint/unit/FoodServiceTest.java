package net.foodsprint.unit;

import net.foodsprint.Exceptions.custom_exceptions.CategoryNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.FoodNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.RestaurantNotFoundException;
import net.foodsprint.domain.Category;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.IngredientsItem;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.repository.CategoryRepository;
import net.foodsprint.repository.FoodRepository;
import net.foodsprint.repository.IngredientsItemRepository;
import net.foodsprint.repository.RestaurantRepository;
import net.foodsprint.service.FoodServiceImpl;
import net.foodsprint.web.dto.request.CreateFoodRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private IngredientsItemRepository ingredientsItemRepository;

    @InjectMocks
    private FoodServiceImpl foodService;

    private Food food;
    private Restaurant restaurant;
    private Category category;
    private CreateFoodRequest request;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Burgers");

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setFoods(new ArrayList<>());

        food = new Food();
        food.setId(1L);
        food.setName("Classic Burger");
        food.setPrice(8L);
        food.setAvailable(true);
        food.setFoodCategory(category);
        food.setRestaurant(restaurant);

        request = new CreateFoodRequest();
        request.setName("Classic Burger");
        request.setDescription("A classic burger");
        request.setPrice(8L);
        request.setCategoryId(1L);
        request.setRestaurantId(1L);
        request.setImages(List.of("burger.png"));
    }

    // ─── createFood ─────────────────────────────────────────────

    @Test
    void givenValidRequest_whenCreateFood_thenFoodIsSaved() {
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(ingredientsItemRepository.findByRestaurantId(1L)).willReturn(List.of());
        given(foodRepository.save(any())).willReturn(food);

        Food result = foodService.createFood(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Classic Burger");
        then(foodRepository).should().save(any());
    }

    @Test
    void givenInvalidCategoryId_whenCreateFood_thenThrowException() {
        given(categoryRepository.findById(99L)).willReturn(Optional.empty());
        request.setCategoryId(99L);

        assertThatThrownBy(() -> foodService.createFood(request))
                .isInstanceOf(CategoryNotFoundException.class);

        then(foodRepository).should(never()).save(any());
    }

    @Test
    void givenInvalidRestaurantId_whenCreateFood_thenThrowException() {
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(restaurantRepository.findById(99L)).willReturn(Optional.empty());
        request.setRestaurantId(99L);

        assertThatThrownBy(() -> foodService.createFood(request))
                .isInstanceOf(RestaurantNotFoundException.class);

        then(foodRepository).should(never()).save(any());
    }

    // ─── findFoodById ────────────────────────────────────────────

    @Test
    void givenValidId_whenFindFoodById_thenReturnFood() {
        given(foodRepository.findById(1L)).willReturn(Optional.of(food));

        Food result = foodService.findFoodById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void givenInvalidId_whenFindFoodById_thenThrowException() {
        given(foodRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.findFoodById(99L))
                .isInstanceOf(FoodNotFoundException.class);
    }

    // ─── getRestaurantsFood ──────────────────────────────────────

    @Test
    void givenRestaurantId_whenGetRestaurantsFood_thenReturnAllFoods() {
        given(foodRepository.findByRestaurantId(1L)).willReturn(List.of(food));

        List<Food> result = foodService.getRestaurantsFood(1L, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void givenFoodCategory_whenGetRestaurantsFood_thenReturnFilteredFoods() {
        Food anotherFood = new Food();
        anotherFood.setId(2L);
        Category anotherCategory = new Category();
        anotherCategory.setName("Salads");
        anotherFood.setFoodCategory(anotherCategory);

        given(foodRepository.findByRestaurantId(1L)).willReturn(List.of(food, anotherFood));

        List<Food> result = foodService.getRestaurantsFood(1L, "Burgers");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFoodCategory().getName()).isEqualTo("Burgers");
    }

    @Test
    void givenNonExistentCategory_whenGetRestaurantsFood_thenReturnEmptyList() {
        given(foodRepository.findByRestaurantId(1L)).willReturn(List.of(food));

        List<Food> result = foodService.getRestaurantsFood(1L, "NonExistent");

        assertThat(result).isEmpty();
    }

    // ─── deleteFood ──────────────────────────────────────────────

    @Test
    void givenValidId_whenDeleteFood_thenRestaurantIsSetNull() {
        given(foodRepository.findById(1L)).willReturn(Optional.of(food));
        given(foodRepository.save(any())).willReturn(food);

        foodService.deleteFood(1L);

        assertThat(food.getRestaurant()).isNull();
        then(foodRepository).should().save(food);
    }

    @Test
    void givenInvalidId_whenDeleteFood_thenThrowException() {
        given(foodRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.deleteFood(99L))
                .isInstanceOf(FoodNotFoundException.class);
    }

    // ─── updateAvailabilityStatus ────────────────────────────────

    @Test
    void givenAvailableFood_whenUpdateAvailability_thenFoodIsUnavailable() {
        food.setAvailable(true);
        given(foodRepository.findById(1L)).willReturn(Optional.of(food));
        given(foodRepository.save(any())).willReturn(food);

        foodService.updateAvailabilityStatus(1L);

        assertThat(food.isAvailable()).isFalse();
    }

    @Test
    void givenUnavailableFood_whenUpdateAvailability_thenFoodIsAvailable() {
        food.setAvailable(false);
        given(foodRepository.findById(1L)).willReturn(Optional.of(food));
        given(foodRepository.save(any())).willReturn(food);

        foodService.updateAvailabilityStatus(1L);

        assertThat(food.isAvailable()).isTrue();
    }
}