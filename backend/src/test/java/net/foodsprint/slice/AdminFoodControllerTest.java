package net.foodsprint.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.foodsprint.Exceptions.custom_exceptions.FoodNotFoundException;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.domain.User;
import net.foodsprint.service.FoodService;
import net.foodsprint.service.RestaurantService;
import net.foodsprint.service.UserService;
import net.foodsprint.web.controller.AdminFoodController;
import net.foodsprint.web.dto.request.CreateFoodRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AdminFoodController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AdminFoodControllerTest {

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
    private Restaurant restaurant;
    private CreateFoodRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setOrders(new ArrayList<>());

        food = new Food();
        food.setId(1L);
        food.setName("Classic Burger");
        food.setPrice(8L);
        food.setAvailable(true);

        request = new CreateFoodRequest();
        request.setName("Classic Burger");
        request.setPrice(8L);
        request.setCategoryId(1L);
        request.setRestaurantId(1L);
        request.setImages(List.of("burger.png"));
    }

    // POST /api/admin/food

    @Test
    void givenValidRequest_whenCreateFood_thenReturn201() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.getRestaurantByUserId(1L)).willReturn(restaurant);
        given(foodService.createFood(any())).willReturn(food);

        mockMvc.perform(post("/api/admin/food")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Classic Burger"));
    }

    @Test
    void givenInvalidRequest_whenCreateFood_thenReturn400() throws Exception {
        request.setName(null); // @NotBlank violation

        mockMvc.perform(post("/api/admin/food")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // DELETE /api/admin/food/{id}

    @Test
    void givenValidId_whenDeleteFood_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        willDoNothing().given(foodService).deleteFood(1L);

        mockMvc.perform(delete("/api/admin/food/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Το προιόν διαγράφηκε"));
    }

    @Test
    void givenInvalidId_whenDeleteFood_thenReturn404() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        willThrow(new FoodNotFoundException("Το προιόν δεν υπάρχει"))
                .given(foodService).deleteFood(99L);

        mockMvc.perform(delete("/api/admin/food/99")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    // PUT /api/admin/food/{id}

    @Test
    void givenValidId_whenUpdateFoodAvailability_thenReturn200() throws Exception {
        food.setAvailable(false);
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(foodService.updateAvailabilityStatus(1L)).willReturn(food);

        mockMvc.perform(put("/api/admin/food/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void givenInvalidId_whenUpdateFoodAvailability_thenReturn404() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        willThrow(new FoodNotFoundException("Το προιόν δεν υπάρχει"))
                .given(foodService).updateAvailabilityStatus(99L);

        mockMvc.perform(put("/api/admin/food/99")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }
}