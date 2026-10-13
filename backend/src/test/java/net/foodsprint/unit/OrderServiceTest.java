package net.foodsprint.unit;

import net.foodsprint.Exceptions.custom_exceptions.OrderNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.StatusNotFoundException;
import net.foodsprint.domain.*;
import net.foodsprint.repository.AddressRepository;
import net.foodsprint.repository.OrderItemRepository;
import net.foodsprint.repository.OrderRepository;
import net.foodsprint.repository.UserRepository;
import net.foodsprint.service.CartService;
import net.foodsprint.service.OrderServiceImpl;
import net.foodsprint.service.RestaurantService;
import net.foodsprint.web.dto.request.OrderRequest;
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
import static org.mockito.BDDMockito.never;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Restaurant restaurant;
    private Cart cart;
    private CartItem cartItem;
    private Food food;
    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setAddresses(new ArrayList<>());

        food = new Food();
        food.setId(1L);
        food.setName("Classic Burger");
        food.setPrice(8L);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setFood(food);
        cartItem.setQuantity(2);
        cartItem.setTotalPrice(16L);
        cartItem.setIngredients(List.of("Cheese"));

        cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(user);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setOrders(new ArrayList<>());

        order = new Order();
        order.setId(1L);
        order.setCustomer(user);
        order.setRestaurant(restaurant);
        order.setOrderStatus("Ετοιμάζεται");
        order.setTotalPrice(16L);

        orderRequest = new OrderRequest();
        orderRequest.setRestaurantId(1L);
        orderRequest.setDeliveryAddress(new Address());
    }

    // createOrder

    @Test
    void givenValidRequest_whenCreateOrder_thenOrderIsSaved() {
        given(addressRepository.save(any())).willReturn(new Address());
        given(restaurantService.findRestaurantById(1L)).willReturn(restaurant);
        given(cartService.findCartByUserId(1L)).willReturn(cart);
        given(orderItemRepository.save(any())).willReturn(new OrderItem());
        given(cartService.calculateCartTotals(cart)).willReturn(16L);
        given(orderRepository.save(any())).willReturn(order);

        Order result = orderService.createOrder(orderRequest, user);

        assertThat(result).isNotNull();
        assertThat(result.getOrderStatus()).isEqualTo("Ετοιμάζεται");
        then(orderRepository).should().save(any());
    }

    // updateOrder

    @Test
    void givenValidStatus_whenUpdateOrder_thenOrderStatusIsUpdated() {
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(orderRepository.save(any())).willReturn(order);

        Order result = orderService.updateOrder(1L, "Έφτασε");

        assertThat(result.getOrderStatus()).isEqualTo("Έφτασε");
        then(orderRepository).should().save(order);
    }

    @Test
    void givenInvalidStatus_whenUpdateOrder_thenThrowException() {
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrder(1L, "INVALID_STATUS"))
                .isInstanceOf(StatusNotFoundException.class);

        then(orderRepository).should(never()).save(any());
    }

    @Test
    void givenInvalidOrderId_whenUpdateOrder_thenThrowException() {
        given(orderRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(99L, "Έφτασε"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    // findOrderById

    @Test
    void givenValidId_whenFindOrderById_thenReturnOrder() {
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        Order result = orderService.findOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void givenInvalidId_whenFindOrderById_thenThrowException() {
        given(orderRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findOrderById(99L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    // getUsersOrder

    @Test
    void givenValidUserId_whenGetUsersOrder_thenReturnOrders() {
        given(orderRepository.findByCustomerId(1L)).willReturn(List.of(order));

        List<Order> result = orderService.getUsersOrder(1L);

        assertThat(result).hasSize(1);
    }

    // getRestaurantsOrder

    @Test
    void givenRestaurantId_whenGetRestaurantsOrder_thenReturnAllOrders() {
        given(orderRepository.findByRestaurantId(1L)).willReturn(List.of(order));

        List<Order> result = orderService.getRestaurantsOrder(1L, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void givenRestaurantIdAndStatus_whenGetRestaurantsOrder_thenReturnFilteredOrders() {
        Order anotherOrder = new Order();
        anotherOrder.setOrderStatus("Ολοκληρώθηκε");

        given(orderRepository.findByRestaurantId(1L)).willReturn(List.of(order, anotherOrder));

        List<Order> result = orderService.getRestaurantsOrder(1L, "Ετοιμάζεται");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderStatus()).isEqualTo("Ετοιμάζεται");
    }
}
