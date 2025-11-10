package iuh.fit.se.dtos.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CartItemRequestDTO {

    @NotNull(message = "Cart ID is required")
    private Long idCart;

    @NotNull(message = "Product ID is required")
    private Long idProduct;

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    public CartItemRequestDTO() {
    }

    public CartItemRequestDTO(Long idCart, Long idProduct, Integer quantity) {
        this.idCart = idCart;
        this.idProduct = idProduct;
        this.quantity = quantity;
    }

    public Long getIdCart() {
        return idCart;
    }

    public void setIdCart(Long idCart) {
        this.idCart = idCart;
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

