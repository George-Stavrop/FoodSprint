package net.foodsprint.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.foodsprint.domain.Order;
import net.foodsprint.domain.User;
import net.foodsprint.service.OrderService;
import net.foodsprint.service.PaymentService;
import net.foodsprint.service.UserService;
import net.foodsprint.web.controller.OrderController;
import net.foodsprint.web.dto.request.OrderRequest;
import net.foodsprint.web.dto.response.PaymentResponse;
import net.foodsprint.domain.Address;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = OrderController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @MockBean
    private PaymentService paymentService;

    private User user;
    private Order order;
    private OrderRequest orderRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        order = new Order();
        order.setId(1L);
        order.setOrderStatus("Ετοιμάζεται");
        order.setTotalPrice(16L);

        orderRequest = new OrderRequest();
        orderRequest.setRestaurantId(1L);
        orderRequest.setDeliveryAddress(new Address());

        paymentResponse = new PaymentResponse();
        paymentResponse.setPayment_url("https://stripe.com/pay/test");
    }

    // POST /api/order

    @Test
    void givenValidRequest_whenCreateOrder_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(orderService.createOrder(any(), any())).willReturn(order);
        given(paymentService.createPaymentLink(any())).willReturn(paymentResponse);

        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_url").value("https://stripe.com/pay/test"));
    }

    @Test
    void givenInvalidRequest_whenCreateOrder_thenReturn400() throws Exception {
        orderRequest.setRestaurantId(null); // @NotNull violation

        mockMvc.perform(post("/api/order")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    // GET /api/order/user

    @Test
    void givenValidJwt_whenGetOrderHistory_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(orderService.getUsersOrder(1L)).willReturn(List.of(order));

        mockMvc.perform(get("/api/order/user")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderStatus").value("Ετοιμάζεται"));
    }

    @Test
    void givenNoJwt_whenGetOrderHistory_thenReturn400() throws Exception {
        mockMvc.perform(get("/api/order/user"))
                .andExpect(status().isBadRequest());
    }
}