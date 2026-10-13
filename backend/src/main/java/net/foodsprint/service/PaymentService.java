package net.foodsprint.service;

import com.stripe.exception.StripeException;
import net.foodsprint.domain.Order;
import net.foodsprint.web.dto.response.PaymentResponse;

public interface PaymentService {

     PaymentResponse createPaymentLink(Order order) throws StripeException;
}
