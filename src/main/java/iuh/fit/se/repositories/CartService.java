package iuh.fit.se.repositories;

import iuh.fit.se.dtos.cart.CartResponseDTO;
import iuh.fit.se.dtos.cartitem.CartItemRequestDTO;
import iuh.fit.se.dtos.cartitem.CartItemResponseDTO;

public interface CartService {

    //Get cart by user ID
    CartResponseDTO getCartByUser(Long userId);

    //Add products to cart
    CartItemResponseDTO addItemToCart(CartItemRequestDTO requestDTO);

    //Update quantity of products in cart
    CartItemResponseDTO updateItemQuantity(Long cartItemId, Integer newQuantity);

    //Delete 1 product from cart
    void removeItemFromCart(Long cartItemId);

    //Clear cart
    void clearCart(Long cartId);
}
