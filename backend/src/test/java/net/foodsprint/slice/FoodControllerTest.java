package net.foodsprint.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.foodsprint.Exceptions.custom_exceptions.FoodNotFoundException;
import net.foodsprint.domain.Category;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.User;
import net.foodsprint.service.FoodService;
import net.foodsprint.service.RestaurantService;
import net.foodsprint.service.UserService;
import net.foodsprint.web.controller.FoodController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FoodController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class FoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FoodService foodService;

    @MockBean
    private UserService userService;

    @MockBean
    private RestaurantService restaurantService;

    private Food food;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        Category category = new Category();
        category.setId(1L);
        category.setName("Burgers");

        food = new Food();
        food.setId(1L);
        food.setName("Classic Burger");
        food.setPrice(8L);
        food.setAvailable(true);
        food.setFoodCategory(category);
    }

    // GET /api/food/restaurant/{restaurantId}

    @Test
    void givenRestaurantId_whenGetRestaurantFood_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(foodService.getRestaurantsFood(1L, null)).willReturn(List.of(food));

        mockMvc.perform(get("/api/food/restaurant/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Classic Burger"));
    }

    @Test
    void givenRestaurantIdAndCategory_whenGetRestaurantFood_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(foodService.getRestaurantsFood(1L, "Burgers")).willReturn(List.of(food));

        mockMvc.perform(get("/api/food/restaurant/1")
                        .header("Authorization", "Bearer token")
                        .param("food_category", "Burgers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Classic Burger"));
    }

    @Test
    void givenNoJwt_whenGetRestaurantFood_thenReturn400() throws Exception {
        mockMvc.perform(get("/api/food/restaurant/1"))
                .andExpect(status().isBadRequest());
    }
}