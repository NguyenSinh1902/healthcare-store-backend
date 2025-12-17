package iuh.fit.se.dtos.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import iuh.fit.se.entities.order.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class OrderRequestDTO {

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Payment method is required") // CASH, CARD
    private PaymentMethod paymentMethod;

    @Pattern(regexp = "^0\\d{9,10}$", message = "Invalid phone number format")
    private String phoneNumber;

    private Long idCoupon;

    private List<Long> selectedCartItemIds;

    @JsonIgnore
    private Double totalAmount;

    @JsonIgnore
    private Double couponDiscount;

    @JsonIgnore
    private Double finalAmount;

    // --- Constructors ---
    public OrderRequestDTO() {}

    public OrderRequestDTO(String deliveryAddress, PaymentMethod paymentMethod, String phoneNumber, Long idCoupon, List<Long> selectedCartItemIds, Double totalAmount, Double couponDiscount, Double finalAmount) {
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.phoneNumber = phoneNumber;
        this.idCoupon = idCoupon;
        this.selectedCartItemIds = selectedCartItemIds;
        this.totalAmount = totalAmount;
        this.couponDiscount = couponDiscount;
        this.finalAmount = finalAmount;
    }

    // --- Getters & Setters ---
    public @NotBlank(message = "Delivery address is required") String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(@NotBlank(message = "Delivery address is required") String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public @NotNull(message = "Payment method is required") PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@NotNull(message = "Payment method is required") PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public @Pattern(regexp = "^0\\d{9,10}$", message = "Invalid phone number format") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Pattern(regexp = "^0\\d{9,10}$", message = "Invalid phone number format") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getIdCoupon() {
        return idCoupon;
    }

    public void setIdCoupon(Long idCoupon) {
        this.idCoupon = idCoupon;
    }

    public List<Long> getSelectedCartItemIds() {
        return selectedCartItemIds;
    }

    public void setSelectedCartItemIds(List<Long> selectedCartItemIds) {
        this.selectedCartItemIds = selectedCartItemIds;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(Double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }
}
