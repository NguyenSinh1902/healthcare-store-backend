package iuh.fit.se.controllers;

import iuh.fit.se.dtos.payment.PaymentRequestDTO;
import iuh.fit.se.dtos.payment.PaymentResponseDTO;
import iuh.fit.se.dtos.payment.PaymentUrlResponseDTO;
import iuh.fit.se.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    //Tạo Payment URL
    @PostMapping("/create_url")
    public ResponseEntity<Map<String, Object>> createPaymentUrl(
            HttpServletRequest request,
            @Valid @RequestBody PaymentRequestDTO requestDTO) {

        PaymentUrlResponseDTO response = paymentService.createVnPayUrl(request, requestDTO);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", response.getMessage());
        body.put("data", response); // Chứa URL

        return ResponseEntity.ok(body);
    }

//    // 2. Callback từ VNPAY
//    @GetMapping("/vnpay_return")
//    public ResponseEntity<Map<String, Object>> vnpayReturn(HttpServletRequest request) {
//
//        try {
//            PaymentResponseDTO response = paymentService.handleVnPayCallback(request);
//
//            // Nếu status là FAILED thì trả về success=false
//            boolean isSuccess = "SUCCESS".equals(response.getStatus());
//
//            Map<String, Object> body = new LinkedHashMap<>();
//            body.put("success", isSuccess);
//            body.put("message", isSuccess ? "Payment successful" : "Payment failed");
//            body.put("data", response);
//
//            // Frontend nhận được cái này sẽ show trang "Thành công" hoặc "Thất bại"
//            return ResponseEntity.ok(body);
//
//        } catch (Exception e) {
//            Map<String, Object> errorBody = new LinkedHashMap<>();
//            errorBody.put("success", false);
//            errorBody.put("message", "Payment error: " + e.getMessage());
//            return ResponseEntity.badRequest().body(errorBody);
//        }
//    }
    // Callback từ VNPAY (Redirect ve Frontend)
    @GetMapping("/vnpay_return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String feUrl = "http://localhost:3000";

        try {
            PaymentResponseDTO result = paymentService.handleVnPayCallback(request);

            if ("SUCCESS".equals(result.getStatus())) {

                String paymentTime = result.getPaymentDate().toString(); //2025-12-14T12:00:00

                // Redirect kèm full thông tin
                String redirectUrl = String.format(
                        "%s/payment/success?orderId=%d&txnRef=%s&paymentDate=%s",
                        feUrl,
                        result.getOrderId(),
                        result.getTransactionRef(),
                        paymentTime
                );
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(feUrl + "/payment/failed?orderId=" + result.getOrderId());
            }

        } catch (Exception e) {
            response.sendRedirect(feUrl + "/payment/error");
        }
    }

}