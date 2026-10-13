package net.foodsprint.unit;

import net.foodsprint.Exceptions.custom_exceptions.CartItemNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.CartNotFoundException;
import net.foodsprint.domain.Cart;
import net.foodsprint.domain.CartItem;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.User;
import net.foodsprint.repository.CartItemRepository;
import net.foodsprint.repository.CartRepository;
import net.foodsprint.service.CartServiceImpl;
import net.foodsprint.service.FoodService;
import net.foodsprint.service.UserService;
import net.foodsprint.web.dto.request.AddCartItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private FoodService foodService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Food food;
    private Cart cart;
    private CartItem cartItem;
    private AddCartItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        food = new Food();
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
        cart.setTotal(0L);
        cart.setItems(new ArrayList<>());

        request = new AddCartItemRequest();
        request.setFoodId(1L);
        request.setQuantity(2);
        request.setIngredients(List.of("Cheese"));
    }

    // addItemToCart

    @Test
    void givenNewFood_whenAddItemToCart_thenCartItemIsSaved() {
        given(userService.findUserByJwtToken("jwt")).willReturn(user);
        given(foodService.findFoodById(1L)).willReturn(food);
        given(cartRepository.findByCustomerId(1L)).willReturn(cart);
        given(cartItemRepository.save(any())).willReturn(cartItem);

        CartItem result = cartService.addItemToCart(request, "jwt");

        assertThat(result).isNotNull();
        assertThat(result.getFood()).isEqualTo(food);
        then(cartItemRepository).should().save(any());
    }

    @Test
    void givenExistingFood_whenAddItemToCart_thenQuantityIsUpdated() {
        // food ήδη υπάρχει στο cart
        cartItem.setQuantity(1);
        cart.getItems().add(cartItem);

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setId(1L);
        updatedCartItem.setFood(food);
        updatedCartItem.setQuantity(3); // 1 + 2
        updatedCartItem.setTotalPrice(24L);

        given(userService.findUserByJwtToken("jwt")).willReturn(user);
        given(foodService.findFoodById(1L)).willReturn(food);
        given(cartRepository.findByCustomerId(1L)).willReturn(cart);
        given(cartItemRepository.findById(1L)).willReturn(Optional.of(cartItem));
        given(cartItemRepository.save(any())).willReturn(updatedCartItem);

        CartItem result = cartService.addItemToCart(request, "jwt");

        assertThat(result.getQuantity()).isEqualTo(3);
    }

    // updateCartItemQuantity

    @Test
    void givenValidCartItemId_whenUpdateQuantity_thenQuantityAndPriceAreUpdated() {
        given(cartItemRepository.findById(1L)).willReturn(Optional.of(cartItem));
        given(cartItemRepository.save(any())).willReturn(cartItem);

        CartItem result = cartService.updateCartItemQuantity(1L, 5);

        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.getTotalPrice()).isEqualTo(40L); // 8 * 5
    }

    @Test
    void givenInvalidCartItemId_whenUpdateQuantity_thenThrowException() {
        given(cartItemRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateCartItemQuantity(99L, 2))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    // removeItemFromCart

    @Test
    void givenValidCartItemId_whenRemoveItemFromCart_thenItemIsRemoved() {
        cart.getItems().add(cartItem);

        given(userService.findUserByJwtToken("jwt")).willReturn(user);
        given(cartRepository.findByCustomerId(1L)).willReturn(cart);
        given(cartItemRepository.findById(1L)).willReturn(Optional.of(cartItem));
        given(cartRepository.save(any())).willReturn(cart);

        Cart result = cartService.removeItemFromCart(1L, "jwt");

        assertThat(result.getItems()).isEmpty();
        then(cartRepository).should().save(cart);
    }

    // calculateCartTotals

    @Test
    void givenCartWithItems_whenCalculateCartTotals_thenReturnCorrectTotal() {
        CartItem item1 = new CartItem();
        item1.setFood(food); // price 8
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        Food food2 = new Food();
        food2.setPrice(5L);
        item2.setFood(food2);
        item2.setQuantity(3);

        cart.setItems(new ArrayList<>(List.of(item1, item2)));

        Long total = cartService.calculateCartTotals(cart);

        assertThat(total).isEqualTo(31L); // (8*2) + (5*3)
    }

    @Test
    void givenEmptyCart_whenCalculateCartTotals_thenReturnZero() {
        Long total = cartService.calculateCartTotals(cart);

        assertThat(total).isEqualTo(0L);
    }

    // findCartById

    @Test
    void givenValidId_whenFindCartById_thenReturnCart() {
        given(cartRepository.findById(1L)).willReturn(Optional.of(cart));

        Cart result = cartService.findCartById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void givenInvalidId_whenFindCartById_thenThrowException() {
        given(cartRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.findCartById(99L))
                .isInstanceOf(CartNotFoundException.class);
    }

    // clearCart

    @Test
    void givenValidUserId_whenClearCart_thenCartIsEmpty() {
        cart.getItems().add(cartItem);

        given(cartRepository.findByCustomerId(1L)).willReturn(cart);
        given(cartRepository.save(any())).willReturn(cart);

        Cart result = cartService.clearCart(1L);

        assertThat(result.getItems()).isEmpty();
        then(cartRepository).should().save(cart);
    }
}