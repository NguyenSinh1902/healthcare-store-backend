package iuh.fit.se.dtos.payment;

import java.time.LocalDateTime;

public class PaymentResponseDTO {
    private Long idPayment;
    private Long orderId;
    private Double amount;
    private String transactionRef;
    private String status; // Trả về String của Enum
    private LocalDateTime paymentDate;

    public PaymentResponseDTO() {}

    // Getters & Setters
    public Long getIdPayment() { return idPayment; }
    public void setIdPayment(Long idPayment) { this.idPayment = idPayment; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}