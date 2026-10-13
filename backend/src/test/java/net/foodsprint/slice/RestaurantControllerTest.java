package net.foodsprint.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.domain.User;
import net.foodsprint.Exceptions.custom_exceptions.RestaurantNotFoundException;
import net.foodsprint.service.RestaurantService;
import net.foodsprint.service.UserService;
import net.foodsprint.web.controller.RestaurantController;
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

@WebMvcTest(value = RestaurantController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private UserService userService;

    private Restaurant restaurant;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCuisineType("Burgers");
        restaurant.setOpen(true);
    }

    // GET /api/restaurants

    @Test
    void givenValidJwt_whenGetAllRestaurants_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.getAllRestaurants()).willReturn(List.of(restaurant));

        mockMvc.perform(get("/api/restaurants")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));
    }

    @Test
    void givenNoJwt_whenGetAllRestaurants_thenReturn400() throws Exception {
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isBadRequest());
    }

    //  /api/restaurants/{id}

    @Test
    void givenValidId_whenFindRestaurantById_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.findRestaurantById(1L)).willReturn(restaurant);

        mockMvc.perform(get("/api/restaurants/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Restaurant"));
    }

    @Test
    void givenInvalidId_whenFindRestaurantById_thenReturn404() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.findRestaurantById(99L))
                .willThrow(new RestaurantNotFoundException(99L));

        mockMvc.perform(get("/api/restaurants/99")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }
}