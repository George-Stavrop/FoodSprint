package net.foodsprint.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.domain.User;
import net.foodsprint.web.dto.request.CreateFoodRequest;
import net.foodsprint.web.dto.response.MessageResponse;
import net.foodsprint.service.FoodService;
import net.foodsprint.service.RestaurantService;
import net.foodsprint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/food")
public class AdminFoodController {

    private final FoodService foodService;

    private final UserService userService;

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Food> createFood(@Valid @RequestBody CreateFoodRequest req,
                                           @RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());
        Food food = foodService.createFood(req);

        return new ResponseEntity<>(food, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<MessageResponse> deleteFood(@PathVariable Long id,
                                           @RequestHeader("Authorization") String jwt) {
        User user = userService.findUserByJwtToken(jwt);

       foodService.deleteFood(id);

        MessageResponse res = new MessageResponse();
        res.setMessage("Το προιόν διαγράφηκε");

       return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Food> updateFoodAvailabilityStatus(@PathVariable Long id,
                                                                        @RequestHeader("Authorization") String jwt) {
        User user = userService.findUserByJwtToken(jwt);

        Food food = foodService.updateAvailabilityStatus(id);

        return new ResponseEntity<>(food, HttpStatus.OK);

    }
}
