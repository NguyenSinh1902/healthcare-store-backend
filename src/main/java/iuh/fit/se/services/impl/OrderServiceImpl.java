package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.order.OrderRequestDTO;
import iuh.fit.se.dtos.order.OrderResponseDTO;
import iuh.fit.se.entities.cart.Cart;
import iuh.fit.se.entities.cartitem.CartItem;
import iuh.fit.se.entities.coupon.Coupon;
import iuh.fit.se.entities.coupon.CouponStatus;
import iuh.fit.se.entities.order.Order;
import iuh.fit.se.entities.order.OrderDetail;
import iuh.fit.se.entities.order.OrderStatus;
import iuh.fit.se.entities.order.PaymentMethod;
import iuh.fit.se.entities.product.Product;
import iuh.fit.se.mappers.OrderDetailMapper;
import iuh.fit.se.mappers.OrderMapper;
import iuh.fit.se.repositories.*;
import iuh.fit.se.services.CartService;
import iuh.fit.se.services.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, CouponRepository couponRepository, ProductRepository productRepository, OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.cartService = cartService;
    }

    //Làm tròn 2 chữ số thập phân
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public OrderResponseDTO createOrderFromCart(Long userId, OrderRequestDTO orderRequestDTO) {
        // Get user cart
        Cart cart = cartRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));

        // Get the CartItem list based on selection
        List<CartItem> cartItems;
        List<Long> selectedIds = orderRequestDTO.getSelectedCartItemIds();

        if (selectedIds != null && !selectedIds.isEmpty()) {
            cartItems = cartItemRepository.findAllById(selectedIds);

            // Security check: Ensure items belong to the current user's cart
            boolean allBelongToUser = cartItems.stream()
                    .allMatch(item -> item.getCart().getIdCart().equals(cart.getIdCart()));

            if (!allBelongToUser) {
                throw new RuntimeException("Security error detected: The selected product is not in your shopping cart.");
            }
        } else {
            throw new RuntimeException("Please select at least one product to proceed to checkout.");
        }

        if (cartItems.isEmpty()) {
            throw new RuntimeException("No products were found to pay for.");
        }

        // CHECK va tru ton kho
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            int buyQuantity = item.getQuantity();

            // CHECK ACTIVE
            if (!product.isActive()) {
                throw new RuntimeException("Sản phẩm '" + product.getNameProduct() + "' đã ngừng kinh doanh. Vui lòng bỏ khỏi giỏ hàng để tiếp tục.");
            }

            // CHECK STOCK
            if (product.getStockQuantity() < buyQuantity) {
                throw new RuntimeException("Sản phẩm '" + product.getNameProduct() + "' không đủ hàng tồn kho (Còn lại: " + product.getStockQuantity() + ")");
            }

            // tru ton kho
            product.setStockQuantity(product.getStockQuantity() - buyQuantity);

            // luu lai
            productRepository.save(product);
        }

        //tinh toan don hang(lam tron)

        // 1. tinh tong tien hang (Raw Total)
        double rawTotal = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // lam tron tong tien hang
        double totalAmount = round(rawTotal);

        // Validate & Apply Coupon
        double couponDiscount = 0.0;
        Coupon coupon = null;

        if (orderRequestDTO.getIdCoupon() != null) {
            coupon = couponRepository.findById(orderRequestDTO.getIdCoupon())
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));

            if (coupon.getStatus() != CouponStatus.ACTIVE) {
                throw new RuntimeException("Coupon is currently inactive.");
            }

            LocalDate now = LocalDate.now();
            if ((coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) ||
                    (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate()))) {
                throw new RuntimeException("Coupon is expired or not yet valid.");
            }

            if (totalAmount < coupon.getMinOrderValue()) {
                throw new RuntimeException("Order total (" + totalAmount + ") is less than minimum requirement (" + coupon.getMinOrderValue() + ") for this coupon.");
            }

            couponDiscount = coupon.getDiscountAmount();
        }

        // 2. Tinh (Final Amount) lam tron
        double finalAmount = round(Math.max(totalAmount - couponDiscount, 0.0));

        // Create Order object
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());

        // Logic Status based on Payment Method
        if (orderRequestDTO.getPaymentMethod() == PaymentMethod.CASH) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            // Nếu chọn thanh toán Card, ban đầu trạng thái là PENDING
            order.setStatus(OrderStatus.PENDING);
        }

        order.setPaymentMethod(orderRequestDTO.getPaymentMethod());

        //xu ly dia chi
        String userProfileAddress = cart.getUser().getAddress();
        String inputAddress = orderRequestDTO.getDeliveryAddress();
        String finalAddress;

        if (inputAddress != null && !inputAddress.trim().isEmpty()) {
            finalAddress = inputAddress;
        } else if (userProfileAddress != null && !userProfileAddress.trim().isEmpty()) {
            finalAddress = userProfileAddress;
        } else {
            throw new RuntimeException("Delivery address is required.");
        }

        //xu ly so dien thoai
        String userProfilePhone = cart.getUser().getPhone(); // Lấy từ User Profile
        String inputPhone = orderRequestDTO.getPhoneNumber(); // Lấy từ form nhập
        String finalPhone;

        //uu tien so nhap tren form
        if (inputPhone != null && !inputPhone.trim().isEmpty()) {
            finalPhone = inputPhone;
        }
        // neu khong co thi lay so tu profile
        else if (userProfilePhone != null && !userProfilePhone.trim().isEmpty()) {
            finalPhone = userProfilePhone;
        }
        // cai nao cung khong co thi bao loi
        else {
            throw new RuntimeException("Phone number is required for delivery.");
        }

        order.setPhoneNumber(finalPhone);
        order.setDeliveryAddress(finalAddress);
        order.setCoupon(coupon);
        order.setTotalAmount(totalAmount);
        order.setCouponDiscount(couponDiscount);
        order.setFinalAmount(finalAmount);

        Order savedOrder = orderRepository.save(order);

        // 3. CartItem => OrderDetail (lam tron TOTAL PRICE)
        List<OrderDetail> orderDetails = cartItems.stream()
                .map(item -> {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrder(savedOrder);
                    detail.setProduct(item.getProduct());
                    detail.setQuantity(item.getQuantity());
                    detail.setUnitPrice(item.getProduct().getPrice());

                    // lam tron total price
                    detail.setTotalPrice(round(item.getProduct().getPrice() * item.getQuantity()));

                    return detail;
                })
                .collect(Collectors.toList());

        orderDetailRepository.saveAll(orderDetails);

        // xu ly cart sau khi tao don hang
        // 1. xoa cac CartItem da chon mua
        cartItemRepository.deleteAll(cartItems);
        cartItemRepository.flush(); // Đẩy lệnh xóa xuống DB ngay để tính toán lại chính xác

        // 2. tinh toan lai tong tien cho gio hang
        List<CartItem> remainingItems = cartItemRepository.findByCart_IdCart(cart.getIdCart());
        double newCartTotalRaw = remainingItems.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        // lam tron tong tien gio hang
        cart.setTotalAmount(round(newCartTotalRaw));
        cartRepository.save(cart);

        // Map response
        if (savedOrder.getOrderDetails() == null) {
            savedOrder.setOrderDetails(new HashSet<>());
        }
        savedOrder.getOrderDetails().addAll(orderDetails);

        return orderMapper.toResponseDTO(savedOrder);
    }


    @Override
    public List<OrderResponseDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUser_IdUser(userId)
                .stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {

        return orderRepository.findById(id)
                .map(orderMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toResponseDTO(updatedOrder);
    }
}