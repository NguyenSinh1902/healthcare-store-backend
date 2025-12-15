package iuh.fit.se.dtos.payment;

public class PaymentUrlResponseDTO {
    private String status;
    private String message;
    private String paymentUrl;

    public PaymentUrlResponseDTO(String status, String message, String paymentUrl) {
        this.status = status;
        this.message = message;
        this.paymentUrl = paymentUrl;
    }
    // Getters Setters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getPaymentUrl() { return paymentUrl; }
}