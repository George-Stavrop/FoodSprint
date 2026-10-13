package net.foodsprint.web.dto.request;

import lombok.Data;

@Data
public class IngredientRequest {
    private Long restaurantId;
    private String name;
    private Long categoryId;
}
