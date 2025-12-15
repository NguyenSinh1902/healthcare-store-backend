package iuh.fit.se.dtos.payment;

import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    // Có thể mở rộng thêm bankCode nếu muốn user chọn bank trước (NCB, VCB...)
    private String bankCode;

    public PaymentRequestDTO() {}
    public PaymentRequestDTO(Long orderId) { this.orderId = orderId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
}