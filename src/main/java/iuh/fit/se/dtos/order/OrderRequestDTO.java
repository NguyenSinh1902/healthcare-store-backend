package iuh.fit.se.dtos.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import iuh.fit.se.entities.order.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderRequestDTO {

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Payment method is required") // CASH, CARD, MOMO
    private PaymentMethod paymentMethod;

    private Long idCoupon; // có thể null nếu không dùng mã giảm giá

    // ⚙️ Các field dưới BE sẽ tự tính, không để FE gửi lên
    @JsonIgnore
    private Double totalAmount;

    @JsonIgnore
    private Double couponDiscount;

    @JsonIgnore
    private Double finalAmount;

    // --- Constructors ---
    public OrderRequestDTO() {}

    public OrderRequestDTO(String deliveryAddress, PaymentMethod paymentMethod, Long idCoupon) {
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.idCoupon = idCoupon;
    }

    // --- Getters & Setters ---
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getIdCoupon() {
        return idCoupon;
    }

    public void setIdCoupon(Long idCoupon) {
        this.idCoupon = idCoupon;
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
