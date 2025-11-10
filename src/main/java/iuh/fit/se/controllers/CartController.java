package iuh.fit.se.controllers;

import iuh.fit.se.dtos.cart.CartResponseDTO;
import iuh.fit.se.dtos.cartitem.CartItemRequestDTO;
import iuh.fit.se.dtos.cartitem.CartItemResponseDTO;
import iuh.fit.se.services.CartItemService;
import iuh.fit.se.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;

    public CartController(CartService cartService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    //Get cart by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getCartByUser(@PathVariable Long userId) {
        CartResponseDTO cart = cartService.getCartByUser(userId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Cart retrieved successfully");
        body.put("data", cart);

        return ResponseEntity.ok(body);
    }

    //Add product to cart
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody CartItemRequestDTO dto) {
        CartItemResponseDTO addedItem = cartService.addItemToCart(dto);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Item added to cart successfully");
        body.put("data", addedItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    //Update quantity
    @PutMapping("/item/{itemId}")
    public ResponseEntity<Map<String, Object>> updateCartItem(@PathVariable Long itemId, @RequestParam Integer quantity) {
        CartItemResponseDTO updated = cartService.updateItemQuantity(itemId, quantity);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Cart item updated successfully");
        body.put("data", updated);

        return ResponseEntity.ok(body);
    }

    //Remove item
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Map<String, Object>> removeItem(@PathVariable Long itemId) {
        cartService.removeItemFromCart(itemId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Category deleted successfully (ID: " + itemId + ")");

        return ResponseEntity.ok(body);
    }

    //Clear cart
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<Map<String, Object>> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Cart cleared successfully");

        return ResponseEntity.ok(body);
    }
}
