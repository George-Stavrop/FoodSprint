package net.foodsprint.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodsprint.domain.Order;
import net.foodsprint.domain.User;
import net.foodsprint.web.dto.request.OrderRequest;
import net.foodsprint.web.dto.response.PaymentResponse;
import net.foodsprint.service.OrderService;
import net.foodsprint.service.PaymentService;
import net.foodsprint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {


    private final OrderService orderService;

    private final UserService userService;

    private final PaymentService paymentService;


    @PostMapping("/order")
    public ResponseEntity<PaymentResponse> createOrder(@Valid @RequestBody OrderRequest req,
                                                       @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.createOrder(req, user);
        PaymentResponse paymentResponse = paymentService.createPaymentLink(order);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }


    @GetMapping("/order/user")
    public ResponseEntity<List<Order>> getOrderHistory(
                                             @RequestHeader("Authorization") String jwt) {

        User user = userService.findUserByJwtToken(jwt);
        List<Order> orders = orderService.getUsersOrder(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }












}
