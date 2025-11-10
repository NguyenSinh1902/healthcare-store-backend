package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.cart.CartResponseDTO;
import iuh.fit.se.dtos.cartitem.CartItemRequestDTO;
import iuh.fit.se.dtos.cartitem.CartItemResponseDTO;
import iuh.fit.se.entities.cart.Cart;
import iuh.fit.se.entities.cartitem.CartItem;
import iuh.fit.se.entities.product.Product;
import iuh.fit.se.exceptions.NotFoundException;
import iuh.fit.se.mappers.CartItemMapper;
import iuh.fit.se.mappers.CartMapper;
import iuh.fit.se.repositories.CartItemRepository;
import iuh.fit.se.repositories.CartRepository;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.repositories.UserRepository;
import iuh.fit.se.services.CartService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           CartMapper cartMapper,
                           CartItemMapper cartItemMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    //Get cart by user ID
    @Override
    public CartResponseDTO getCartByUser(Long userId) {
        Cart cart = cartRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user ID: " + userId));
        return cartMapper.toResponseDTO(cart);
    }

    //Add product to cart
    @Transactional
    @Override
    public CartItemResponseDTO addItemToCart(CartItemRequestDTO requestDTO) {
        Cart cart = cartRepository.findById(requestDTO.getIdCart())
                .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + requestDTO.getIdCart()));

        Product product = productRepository.findById(requestDTO.getIdProduct())
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + requestDTO.getIdProduct()));

        //Check if the product is in the cart
        CartItem existingItem = cartItemRepository.findByCart_IdCartAndProduct_IdProduct(cart.getIdCart(), product.getIdProduct())
                .orElse(null);

        CartItem cartItem;
        if (existingItem != null) {
            //If already present, add quantity
            existingItem.setQuantity(existingItem.getQuantity() + requestDTO.getQuantity());
            existingItem.setTotalPrice(existingItem.getQuantity() * product.getPrice());
            cartItem = cartItemRepository.save(existingItem);
        } else {
            //If not, add new
            cartItem = cartItemMapper.toEntity(requestDTO);
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setTotalPrice(product.getPrice() * requestDTO.getQuantity());
            cartItemRepository.save(cartItem);
        }

        updateCartTotal(cart);

        return cartItemMapper.toResponseDTO(cartItem);
    }

    //Update quantity
    @Transactional
    @Override
    public CartItemResponseDTO updateItemQuantity(Long cartItemId, Integer newQuantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found with ID: " + cartItemId));

        if (newQuantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(newQuantity);
            item.setTotalPrice(item.getProduct().getPrice() * newQuantity);
            cartItemRepository.save(item);
        }

        updateCartTotal(item.getCart());
        return cartItemMapper.toResponseDTO(item);
    }

    //Delete product
    @Transactional
    @Override
    public void removeItemFromCart(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found with ID: " + cartItemId));

        Cart cart = item.getCart();
        cartItemRepository.delete(item);
        updateCartTotal(cart);
    }

    //Clear Cart
    @Transactional
    @Override
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
    }

    //Update cart total
    private void updateCartTotal(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCart_IdCart(cart.getIdCart());
        double total = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }
}
