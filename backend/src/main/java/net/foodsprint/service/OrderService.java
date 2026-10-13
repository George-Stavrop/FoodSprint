package net.foodsprint.service;

import net.foodsprint.domain.Order;
import net.foodsprint.domain.User;
import net.foodsprint.web.dto.request.OrderRequest;

import java.util.List;

public interface OrderService {

     Order createOrder(OrderRequest order, User user);

     Order updateOrder(Long orderId, String orderStatus);

     List<Order> getUsersOrder(Long userId);

     List<Order> getRestaurantsOrder(Long restaurantId, String oderStatus);

     Order findOrderById(Long orderId);
}
