package iuh.fit.se.entities.payment;

import iuh.fit.se.entities.order.Order;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPayment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "order_info_code")
    private String orderInfoCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    public Payment() {}

    public Payment(Order order, Double amount, String orderInfoCode, PaymentStatus status) {
        this.order = order;
        this.amount = amount;
        this.orderInfoCode = orderInfoCode;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getIdPayment() { return idPayment; }
    public void setIdPayment(Long idPayment) { this.idPayment = idPayment; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public String getOrderInfoCode() { return orderInfoCode; }
    public void setOrderInfoCode(String orderInfoCode) { this.orderInfoCode = orderInfoCode; }
}