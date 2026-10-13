package net.foodsprint.service;

import net.foodsprint.domain.Cart;
import net.foodsprint.domain.CartItem;
import net.foodsprint.web.dto.request.AddCartItemRequest;

public interface CartService {

     CartItem addItemToCart(AddCartItemRequest req, String jwt);

     CartItem updateCartItemQuantity(Long cartItemId, int quantity);

     Cart removeItemFromCart(Long cartItemId, String jwt);

     Long calculateCartTotals(Cart cart);

     Cart findCartById(Long id);

     Cart findCartByUserId(Long userId);

     Cart clearCart(Long userId);
}
