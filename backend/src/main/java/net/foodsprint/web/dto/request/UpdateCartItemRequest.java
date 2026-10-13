package net.foodsprint.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull
    private Long cartItemId;

    @Positive
    private int quantity;
}
