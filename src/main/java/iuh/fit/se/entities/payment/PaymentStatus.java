package iuh.fit.se.entities.payment;

public enum PaymentStatus {
    PENDING, // Đang chờ thanh toán (lúc mới tạo URL)
    SUCCESS, // Thanh toán thành công (VNPAY trả về 00)
    FAILED   // Thanh toán thất bại
}