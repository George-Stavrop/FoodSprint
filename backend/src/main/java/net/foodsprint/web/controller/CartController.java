package net.foodsprint.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.foodsprint.domain.Cart;
import net.foodsprint.domain.CartItem;
import net.foodsprint.domain.User;
import net.foodsprint.web.dto.request.AddCartItemRequest;
import net.foodsprint.web.dto.request.UpdateCartItemRequest;
import net.foodsprint.service.CartService;
import net.foodsprint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Cart> getCart(
            @RequestHeader("Authorization") String jwt)  {

        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findCartByUserId(user.getId());
        return new ResponseEntity<>(cart, HttpStatus.OK);

    }

    @PostMapping("/items")
    public ResponseEntity<CartItem> addItemToCart(@Valid @RequestBody AddCartItemRequest req,
                                                  @RequestHeader("Authorization") String jwt) {

        CartItem cartItem = cartService.addItemToCart(req, jwt);
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    @PutMapping("/items")
    public ResponseEntity<CartItem> updateItem(
            @Valid @RequestBody UpdateCartItemRequest req,
            @RequestHeader("Authorization") String jwt) {

        CartItem cartItem = cartService.updateCartItemQuantity(req.getCartItemId(), req.getQuantity());
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Cart> removeCartItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt)  {

        Cart cart = cartService.removeItemFromCart(id, jwt);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Cart> clearCart(
            @RequestHeader("Authorization") String jwt)  {

        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.clearCart(user.getId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }





}
