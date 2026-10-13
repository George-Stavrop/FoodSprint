package net.foodsprint.web.dto.request;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class AddCartItemRequest {

    @NotNull
    private Long foodId;

    @NotNull
    @Positive
    private int quantity;

    @ElementCollection
    private List<String> ingredients;
}
