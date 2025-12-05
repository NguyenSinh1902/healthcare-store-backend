package iuh.fit.se.dtos.coupon;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class CouponRequestDTO {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    private String code;

    @NotNull(message = "Discount amount is required")
    @Positive(message = "Discount amount must be greater than 0")
    private Double discountAmount;

    @NotNull(message = "Minimum order value is required")
    @PositiveOrZero(message = "Minimum order value must be >= 0")
    private Double minOrderValue;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private boolean active = true; // default true

    public CouponRequestDTO() {}

    public CouponRequestDTO(String code, Double discountAmount, Double minOrderValue, LocalDate startDate, LocalDate endDate, boolean active) {
        this.code = code;
        this.discountAmount = discountAmount;
        this.minOrderValue = minOrderValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    // Getters và Setters

    public @NotBlank(message = "Coupon code is required") @Size(max = 50, message = "Coupon code must not exceed 50 characters") String getCode() {
        return code;
    }

    public void setCode(@NotBlank(message = "Coupon code is required") @Size(max = 50, message = "Coupon code must not exceed 50 characters") String code) {
        this.code = code;
    }

    public @NotNull(message = "Discount amount is required") @Positive(message = "Discount amount must be greater than 0") Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(@NotNull(message = "Discount amount is required") @Positive(message = "Discount amount must be greater than 0") Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public @NotNull(message = "Minimum order value is required") @PositiveOrZero(message = "Minimum order value must be >= 0") Double getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(@NotNull(message = "Minimum order value is required") @PositiveOrZero(message = "Minimum order value must be >= 0") Double minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public @NotNull(message = "Start date is required") LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "Start date is required") LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "End date is required") LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull(message = "End date is required") LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
