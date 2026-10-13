package net.foodsprint.web.controller;

import lombok.RequiredArgsConstructor;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.User;
import net.foodsprint.service.FoodService;
import net.foodsprint.service.RestaurantService;
import net.foodsprint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService foodService;

    private final UserService userService;

    private final RestaurantService restaurantService;


    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Food>> getRestaurantFood(

            @RequestParam(required = false) String food_category,
            @PathVariable Long restaurantId,
            @RequestHeader("Authorization") String jwt)  {

        User user = userService.findUserByJwtToken(jwt);

        List<Food> food = foodService.getRestaurantsFood(restaurantId,food_category);


        return new ResponseEntity<>(food, HttpStatus.OK);
    }
}
