package net.foodsprint.web.dto.request;

import jakarta.validation.constraints.NotNull;
import net.foodsprint.domain.Address;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull
    private Long restaurantId;

    @NotNull
    private Address deliveryAddress;
}
