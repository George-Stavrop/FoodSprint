package net.foodsprint.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodsprint.domain.IngredientCategory;
import net.foodsprint.domain.IngredientsItem;
import net.foodsprint.web.dto.request.IngredientCategoryRequest;
import net.foodsprint.web.dto.request.IngredientRequest;
import net.foodsprint.service.IngredientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/ingredients")
public class IngredientController {

    private final IngredientsService ingredientsService;

    @PostMapping("/category")
    public ResponseEntity<IngredientCategory> createIngredientCategory(
            @Valid @RequestBody  IngredientCategoryRequest req
    ) {

        IngredientCategory item = ingredientsService.createIngredientCategory(req.getName(), req.getRestaurantId());

        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }


    @PostMapping()
    public ResponseEntity<IngredientsItem> createIngredientItem(
            @Valid @RequestBody IngredientRequest req
    )  {

        IngredientsItem item = ingredientsService.createIngredientItem(req.getRestaurantId(), req.getName(),req.getCategoryId());

        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<IngredientsItem> updateIngredientStock(
            @PathVariable Long id
    )  {

        IngredientsItem item = ingredientsService.updateStock(id);
        return new ResponseEntity<>(item, HttpStatus.OK);

    }


    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<IngredientsItem>> getRestaurantIngredients(
            @PathVariable Long id
    )  {

        List<IngredientsItem> items = ingredientsService.findRestaurantsIngredients(id);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }


    @GetMapping("/restaurant/{id}/category")
    public ResponseEntity<List<IngredientCategory>> getRestaurantIngredientCategory(
        @PathVariable Long id
    )  {

        List<IngredientCategory> items = ingredientsService.findIngredientCategoryByRestaurantId(id);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
