package net.foodsprint.slice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.foodsprint.Exceptions.custom_exceptions.OrderNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.StatusNotFoundException;
import net.foodsprint.domain.Order;
import net.foodsprint.domain.User;
import net.foodsprint.service.OrderService;
import net.foodsprint.service.UserService;
import net.foodsprint.web.controller.AdminOrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AdminOrderController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        order = new Order();
        order.setId(1L);
        order.setOrderStatus("Ετοιμάζεται");
        order.setTotalPrice(16L);
    }

    // GET /api/admin/order/restaurant/{id}

    @Test
    void givenRestaurantId_whenGetOrderHistory_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(orderService.getRestaurantsOrder(1L, null)).willReturn(List.of(order));

        mockMvc.perform(get("/api/admin/order/restaurant/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderStatus").value("Ετοιμάζεται"));
    }

    @Test
    void givenRestaurantIdAndStatus_whenGetOrderHistory_thenReturn200() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(orderService.getRestaurantsOrder(1L, "Ετοιμάζεται")).willReturn(List.of(order));

        mockMvc.perform(get("/api/admin/order/restaurant/1")
                        .header("Authorization", "Bearer token")
                        .param("orderStatus", "Ετοιμάζεται"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value("Ετοιμάζεται"));
    }

    // PUT /api/admin/order/{id}/{orderStatus}

    @Test
    void givenValidStatus_whenUpdateOrderStatus_thenReturn200() throws Exception {
        order.setOrderStatus("Έφτασε");
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        given(orderService.updateOrder(1L, "Έφτασε")).willReturn(order);

        mockMvc.perform(put("/api/admin/order/1/Έφτασε")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("Έφτασε"));
    }

    @Test
    void givenInvalidStatus_whenUpdateOrderStatus_thenReturn404() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        willThrow(new StatusNotFoundException("Invalid status"))
                .given(orderService).updateOrder(1L, "INVALID");

        mockMvc.perform(put("/api/admin/order/1/INVALID")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidOrderId_whenUpdateOrderStatus_thenReturn404() throws Exception {
        given(userService.findUserByJwtToken("Bearer token")).willReturn(user);
        willThrow(new OrderNotFoundException("Η παραγγελία δεν βρέθηκε"))
                .given(orderService).updateOrder(99L, "Έφτασε");

        mockMvc.perform(put("/api/admin/order/99/Έφτασε")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }
}