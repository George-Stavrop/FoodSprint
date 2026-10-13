package net.foodsprint.service;

import lombok.RequiredArgsConstructor;
import net.foodsprint.Exceptions.custom_exceptions.CartItemNotFoundException;
import net.foodsprint.Exceptions.custom_exceptions.CartNotFoundException;
import net.foodsprint.web.dto.request.AddCartItemRequest;
import net.foodsprint.domain.Cart;
import net.foodsprint.domain.CartItem;
import net.foodsprint.domain.Food;
import net.foodsprint.domain.User;
import net.foodsprint.repository.CartItemRepository;
import net.foodsprint.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final UserService userService;

    private final CartItemRepository cartItemRepository;

    private final FoodService foodService;

    @Override
    public CartItem addItemToCart(AddCartItemRequest req, String jwt)  {
        User user = userService.findUserByJwtToken(jwt);

        Food food = foodService.findFoodById(req.getFoodId());

        Cart cart = cartRepository.findByCustomerId(user.getId());

        for (CartItem cartItem : cart.getItems()){
            if (cartItem.getFood().equals(food)) {
                int newQuantity = cartItem.getQuantity() + req.getQuantity();
                return updateCartItemQuantity(cartItem.getId(), newQuantity);
            }
        }


        CartItem newCartItem = new CartItem();
        newCartItem.setFood(food);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(req.getQuantity());
        newCartItem.setIngredients(req.getIngredients());
        newCartItem.setTotalPrice(req.getQuantity() * food.getPrice());

        CartItem savedCartItem = cartItemRepository.save(newCartItem);

        cart.getItems().add(savedCartItem);

        return savedCartItem;
    }

    @Override
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {

        CartItem cartItem = findCartItemById(cartItemId);
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getFood().getPrice() * quantity);

        return cartItemRepository.save(cartItem);
    }

    @Override
    public Cart removeItemFromCart(Long cartItemId, String jwt) {

        User user = userService.findUserByJwtToken(jwt);

        Cart cart = cartRepository.findByCustomerId(user.getId());

       CartItem cartItem = findCartItemById(cartItemId);

        cart.getItems().remove(cartItem);

        return cartRepository.save(cart);
    }

    public CartItem findCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("Το προιόν δεν υπάρχει"));
    }

    @Override
    public Long calculateCartTotals(Cart cart) {
        long total = 0L;

        for (CartItem cartItem: cart.getItems()) {
            total += cartItem.getFood().getPrice()*cartItem.getQuantity();
        }
        return total;
    }

    @Override
    public Cart findCartById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException("Το καλάθι δεν βρέθηκε"));
    }

    @Override
    public Cart findCartByUserId(Long userId) {
        
        Cart cart = cartRepository.findByCustomerId(userId);
        cart.setTotal((calculateCartTotals(cart)));
        return cart;
    }

    @Override
    public Cart clearCart(Long userId) {
        
        Cart cart = findCartByUserId(userId);

        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}
