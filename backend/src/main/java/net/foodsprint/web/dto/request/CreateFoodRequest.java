package net.foodsprint.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import net.foodsprint.domain.IngredientsItem;
import lombok.Data;

import java.util.List;

@Data
public class CreateFoodRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Positive
    private Long price;

    @NotNull
    private Long categoryId;

    @NotEmpty
    private List<String> images;

    @NotNull
    private Long restaurantId;

    private List<IngredientsItem> ingredients;
}
