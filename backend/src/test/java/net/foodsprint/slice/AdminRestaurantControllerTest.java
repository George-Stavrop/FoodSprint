package net.foodsprint.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.foodsprint.Exceptions.custom_exceptions.RestaurantNotFoundException;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.domain.User;
import net.foodsprint.service.RestaurantService;
import net.foodsprint.service.UserService;
import net.foodsprint.web.controller.AdminRestaurantController;
import net.foodsprint.web.dto.request.CreateRestaurantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AdminRestaurantController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AdminRestaurantControllerTest {

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
    private CreateRestaurantRequest request;

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

        request = new CreateRestaurantRequest();
        request.setName("Test Restaurant");
        request.setCuisineType("Burgers");
        request.setDescription("Test description");
        request.setImages(List.of("image.png"));
    }

    // POST /api/admin/restaurants

    @Test
    void givenValidRequest_whenCreateRestaurant_thenReturn201() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.createRestaurant(any(), any())).willReturn(restaurant);

        mockMvc.perform(post("/api/admin/restaurants")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Restaurant"));
    }

    // PUT /api/admin/restaurants/{id}

    @Test
    void givenValidRequest_whenUpdateRestaurant_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.updateRestaurant(eq(1L), any())).willReturn(restaurant);

        mockMvc.perform(put("/api/admin/restaurants/1")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void givenInvalidId_whenUpdateRestaurant_thenReturn404() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.updateRestaurant(eq(99L), any()))
                .willThrow(new RestaurantNotFoundException(99L));

        mockMvc.perform(put("/api/admin/restaurants/99")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // DELETE /api/admin/restaurants/{id}

    @Test
    void givenValidId_whenDeleteRestaurant_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        willDoNothing().given(restaurantService).deleteRestaurant(1L);

        mockMvc.perform(delete("/api/admin/restaurants/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Το εστιατόριο διαγράφηκε επιτυχώς"));
    }

    @Test
    void givenInvalidId_whenDeleteRestaurant_thenReturn404() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        willThrow(new RestaurantNotFoundException(99L))
                .given(restaurantService).deleteRestaurant(99L);

        mockMvc.perform(delete("/api/admin/restaurants/99")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    // PUT /api/admin/restaurants/{id}/status

    @Test
    void givenValidId_whenUpdateRestaurantStatus_thenReturn200() throws Exception {
        restaurant.setOpen(false);
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.updateRestaurantStatus(1L)).willReturn(restaurant);

        mockMvc.perform(put("/api/admin/restaurants/1/status")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.open").value(false));
    }

    // GET /api/admin/restaurants/user

    @Test
    void givenValidJwt_whenFindRestaurantByUserId_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.getRestaurantByUserId(1L)).willReturn(restaurant);

        mockMvc.perform(get("/api/admin/restaurants/user")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Restaurant"));
    }

    @Test
    void givenInvalidJwt_whenFindRestaurantByUserId_thenReturn404() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(restaurantService.getRestaurantByUserId(1L))
                .willThrow(new RestaurantNotFoundException(1L));

        mockMvc.perform(get("/api/admin/restaurants/user")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }
}