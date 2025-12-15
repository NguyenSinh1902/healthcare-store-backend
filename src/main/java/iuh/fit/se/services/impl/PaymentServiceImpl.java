package iuh.fit.se.services.impl;

import iuh.fit.se.configs.VnPayConfig;
import iuh.fit.se.dtos.payment.PaymentRequestDTO;
import iuh.fit.se.dtos.payment.PaymentResponseDTO;
import iuh.fit.se.dtos.payment.PaymentUrlResponseDTO;
import iuh.fit.se.entities.order.Order;
import iuh.fit.se.entities.order.OrderStatus;
import iuh.fit.se.entities.payment.Payment;
import iuh.fit.se.entities.payment.PaymentStatus;
import iuh.fit.se.mappers.PaymentMapper;
import iuh.fit.se.repositories.OrderRepository;
import iuh.fit.se.repositories.PaymentRepository;
import iuh.fit.se.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(OrderRepository orderRepository, PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentUrlResponseDTO createVnPayUrl(HttpServletRequest req, PaymentRequestDTO requestDTO) {
        Order order = orderRepository.findById(requestDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + requestDTO.getOrderId()));

        if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Order has already been processed or paid.");
        }

        double exchangeRate = 25000.0;

        double amountInVND = order.getFinalAmount() * exchangeRate;

        long amount = (long) (amountInVND * 100);

        String vnp_TxnRef = order.getIdOrder() + "_" + System.currentTimeMillis();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (requestDTO.getBankCode() != null && !requestDTO.getBankCode().isEmpty()) {
            vnp_Params.put("vnp_BankCode", requestDTO.getBankCode());
        } else {
            vnp_Params.put("vnp_BankCode", "NCB"); // Default demo
        }

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Pay for order #" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", VnPayConfig.getIpAddress(req));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Build URL & Signature
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;

        return new PaymentUrlResponseDTO("OK", "Successfully generated URL", paymentUrl);
    }

    @Override
    @Transactional
    public PaymentResponseDTO handleVnPayCallback(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        String amountStr = request.getParameter("vnp_Amount");

        if (status == null || vnp_TxnRef == null) {
            throw new RuntimeException("Invalid callback parameters");
        }

        String orderIdStr;
        if (vnp_TxnRef.contains("_")) {
            String[] parts = vnp_TxnRef.split("_");
            orderIdStr = parts[0];
        } else {
            orderIdStr = vnp_TxnRef;
        }
        Long orderId = Long.parseLong(orderIdStr);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment;
        Optional<Payment> existingPayment = paymentRepository.findByOrder_IdOrder(orderId);

        if (existingPayment.isPresent()) {
            payment = existingPayment.get();
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                return paymentMapper.toResponseDTO(payment);
            }
        } else {
            payment = new Payment();
            payment.setOrder(order);
        }

        payment.setOrderInfoCode(vnp_TxnRef);
        payment.setTransactionRef(transactionNo);

        if (amountStr != null) {

            payment.setAmount(Double.parseDouble(amountStr) / 100);
        } else {
            payment.setAmount(order.getFinalAmount());
        }

        if ("00".equals(status)) {

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());

            // Update Order thành CONFIRMED
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            System.out.println("Thanh toán thành công: Order " + orderId);

        } else if ("24".equals(status)) {

            payment.setStatus(PaymentStatus.FAILED);

            System.out.println("Khách hàng đã hủy thanh toán (Mã lỗi " + status + "): Order " + orderId);


        } else {

            payment.setStatus(PaymentStatus.FAILED);

            System.out.println("Thanh toán thất bại (Lỗi " + status + "): Order " + orderId);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponseDTO(savedPayment);
    }

}