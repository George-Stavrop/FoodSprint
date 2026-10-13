package net.foodsprint.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.foodsprint.Exceptions.custom_exceptions.CartItemNotFoundException;
import net.foodsprint.domain.Cart;
import net.foodsprint.domain.CartItem;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.User;
import net.foodsprint.service.CartService;
import net.foodsprint.service.UserService;
import net.foodsprint.web.controller.CartController;
import net.foodsprint.web.dto.request.AddCartItemRequest;
import net.foodsprint.web.dto.request.UpdateCartItemRequest;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CartController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;

    private User user;
    private Cart cart;
    private CartItem cartItem;
    private AddCartItemRequest addRequest;
    private UpdateCartItemRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        Food food = new Food();
        food.setId(1L);
        food.setName("Classic Burger");
        food.setPrice(8L);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setFood(food);
        cartItem.setQuantity(2);
        cartItem.setTotalPrice(16L);

        cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(user);
        cart.setTotal(16L);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        addRequest = new AddCartItemRequest();
        addRequest.setFoodId(1L);
        addRequest.setQuantity(2);

        updateRequest = new UpdateCartItemRequest();
        updateRequest.setCartItemId(1L);
        updateRequest.setQuantity(3);
    }

    // GET /api/cart

    @Test
    void givenValidJwt_whenGetCart_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(cartService.findCartByUserId(1L)).willReturn(cart);

        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.total").value(16L));
    }

    @Test
    void givenNoJwt_whenGetCart_thenReturn400() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isBadRequest());
    }

    // POST /api/cart/items

    @Test
    void givenValidRequest_whenAddItemToCart_thenReturn200() throws Exception {
        given(cartService.addItemToCart(any(), eq("Bearer token"))).willReturn(cartItem);

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void givenInvalidRequest_whenAddItemToCart_thenReturn400() throws Exception {
        addRequest.setFoodId(null); // @NotNull violation

        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isBadRequest());
    }

    // PUT /api/cart/items

    @Test
    void givenValidRequest_whenUpdateItem_thenReturn200() throws Exception {
        cartItem.setQuantity(3);
        cartItem.setTotalPrice(24L);
        given(cartService.updateCartItemQuantity(1L, 3)).willReturn(cartItem);

        mockMvc.perform(put("/api/cart/items")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.totalPrice").value(24L));
    }

    @Test
    void givenInvalidCartItemId_whenUpdateItem_thenReturn404() throws Exception {
        updateRequest.setCartItemId(99L);
        willThrow(new CartItemNotFoundException("Το προιόν δεν υπάρχει"))
                .given(cartService).updateCartItemQuantity(99L, 3);

        mockMvc.perform(put("/api/cart/items")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    // DELETE /api/cart/items/{id}

    @Test
    void givenValidId_whenRemoveCartItem_thenReturn200() throws Exception {
        cart.setItems(new ArrayList<>());
        given(cartService.removeItemFromCart(1L, "Bearer token")).willReturn(cart);

        mockMvc.perform(delete("/api/cart/items/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void givenInvalidId_whenRemoveCartItem_thenReturn404() throws Exception {
        willThrow(new CartItemNotFoundException("Το προιόν δεν υπάρχει"))
                .given(cartService).removeItemFromCart(99L, "Bearer token");

        mockMvc.perform(delete("/api/cart/items/99")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    // DELETE /api/cart/items

    @Test
    void givenValidJwt_whenClearCart_thenReturn200() throws Exception {
        cart.setItems(new ArrayList<>());
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(cartService.clearCart(1L)).willReturn(cart);

        mockMvc.perform(delete("/api/cart/items")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}