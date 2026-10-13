package net.foodsprint.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodsprint.domain.Restaurant;
import net.foodsprint.domain.User;
import net.foodsprint.web.dto.request.CreateRestaurantRequest;
import net.foodsprint.web.dto.response.MessageResponse;
import net.foodsprint.service.RestaurantService;
import net.foodsprint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {

    private final RestaurantService restaurantService;

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<Restaurant> createRestaurant(
           @Valid @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt
            ) {

        User user = userService.findUserByJwtToken(jwt);

        Restaurant restaurant = restaurantService.createRestaurant(req, user);

        return new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @Valid @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) {
        User user = userService.findUserByJwtToken(jwt);

        Restaurant restaurant = restaurantService.updateRestaurant(id, req);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRestaurant(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) {
        User user = userService.findUserByJwtToken(jwt);

         restaurantService.deleteRestaurant(id);
         MessageResponse res = new MessageResponse();
         res.setMessage("Το εστιατόριο διαγράφηκε επιτυχώς");

         return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Restaurant> updateRestaurantStatus(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) {
        User user = userService.findUserByJwtToken(jwt);

        Restaurant restaurant = restaurantService.updateRestaurantStatus(id);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<Restaurant> findRestaurantByUserId(
            @RequestHeader("Authorization") String jwt
    )  {

        User user = userService.findUserByJwtToken(jwt);

       Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());

       return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }



}
