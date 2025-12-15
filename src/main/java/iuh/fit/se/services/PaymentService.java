package iuh.fit.se.services;

import iuh.fit.se.dtos.payment.PaymentRequestDTO;
import iuh.fit.se.dtos.payment.PaymentResponseDTO;
import iuh.fit.se.dtos.payment.PaymentUrlResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    // Tạo URL
    PaymentUrlResponseDTO createVnPayUrl(HttpServletRequest req, PaymentRequestDTO requestDTO);

    // Xử lý callback
    PaymentResponseDTO handleVnPayCallback(HttpServletRequest req);
}