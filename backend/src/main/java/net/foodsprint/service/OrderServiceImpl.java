package net.foodsprint.service;

import lombok.RequiredArgsConstructor;
import net.foodsprint.Exceptions.custom_exceptions.OrderNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.StatusNotFoundException;
import net.foodsprint.domain.*;
import net.foodsprint.web.dto.request.OrderRequest;
import net.foodsprint.repository.AddressRepository;
import net.foodsprint.repository.OrderItemRepository;
import net.foodsprint.repository.OrderRepository;
import net.foodsprint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    private final RestaurantService restaurantService;

    private final CartService cartService;


    @Override
    public Order createOrder(OrderRequest order, User user) {

        Address shipAddress = order.getDeliveryAddress();

        Address savedAddress = addressRepository.save(shipAddress);

        if (!user.getAddresses().contains(savedAddress)) {
            user.getAddresses().add(savedAddress);
        }

        Restaurant restaurant = restaurantService.findRestaurantById(order.getRestaurantId());

        Order createdOrder = new Order();
        createdOrder.setCustomer(user);
        createdOrder.setCreatedAt(new Date());
        createdOrder.setOrderStatus("Ετοιμάζεται");
        createdOrder.setDeliveryAddress(savedAddress);
        createdOrder.setRestaurant(restaurant);


        Cart cart = cartService.findCartByUserId(user.getId());

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setIngredients(cartItem.getIngredients());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(savedOrderItem);
        }

        Long totalPrice = cartService.calculateCartTotals(cart);

        createdOrder.setItems(orderItems);
        createdOrder.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(createdOrder);
        restaurant.getOrders().add(savedOrder);

        return createdOrder;
    }

    @Override
    public Order updateOrder(Long orderId, String orderStatus) {
        Order order  = findOrderById(orderId);
        
        if (orderStatus.equals("Σε_διανομή")
                || orderStatus.equals("Ολοκληρώθηκε")
                || orderStatus.equals("Έφτασε")
                || orderStatus.equals("Ετοιμάζεται")
        ) {
            order.setOrderStatus(orderStatus);
            return orderRepository.save(order);
        }
        throw new StatusNotFoundException("Please select a valid order Status");
    }



    @Override
    public List<Order> getUsersOrder(Long userId) {
        return orderRepository.findByCustomerId(userId);
    }

    @Override
    public List<Order> getRestaurantsOrder(Long restaurantId, String orderStatus) {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        if (orderStatus != null) {
            orders = orders.stream().filter(order -> order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());
        }

        return orders;
    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Η παραγγελία δεν βρέθηκε"));
    }

}
