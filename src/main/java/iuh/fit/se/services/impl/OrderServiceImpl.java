package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.order.OrderRequestDTO;
import iuh.fit.se.dtos.order.OrderResponseDTO;
import iuh.fit.se.entities.cart.Cart;
import iuh.fit.se.entities.cartitem.CartItem;
import iuh.fit.se.entities.coupon.Coupon;
import iuh.fit.se.entities.order.Order;
import iuh.fit.se.entities.order.OrderDetail;
import iuh.fit.se.entities.order.OrderStatus;
import iuh.fit.se.mappers.OrderDetailMapper;
import iuh.fit.se.mappers.OrderMapper;
import iuh.fit.se.repositories.*;
import iuh.fit.se.services.CartService;
import iuh.fit.se.services.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, CouponRepository couponRepository, OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.cartService = cartService;
    }

    @Override
    public OrderResponseDTO createOrderFromCart(Long userId, OrderRequestDTO orderRequestDTO) {
        // Get user cart
        Cart cart = cartRepository.findByUser_IdUser(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));

        // Get the CartItem list
        List<CartItem> cartItems = cartItemRepository.findByCart_IdCart(cart.getIdCart());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // Calculate total amount
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // Apply coupon code
        double couponDiscount = 0.0;
        Coupon coupon = null;
        if (orderRequestDTO.getIdCoupon() != null) {
            coupon = couponRepository.findById(orderRequestDTO.getIdCoupon())
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));
            couponDiscount = coupon.getDiscountAmount() != null ? coupon.getDiscountAmount() : 0.0;
        }

        // [BUG TIỀM ẨN]: Nếu coupon > totalAmount thì finalAmount sẽ bị âm
        double finalAmount = totalAmount - couponDiscount;

        // Create Order object
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentMethod(orderRequestDTO.getPaymentMethod());
        // Lấy địa chỉ giao hàng hoặc địa chỉ mặc định
        order.setDeliveryAddress(
                (orderRequestDTO.getDeliveryAddress() != null && !orderRequestDTO.getDeliveryAddress().isBlank())
                        ? orderRequestDTO.getDeliveryAddress()
                        : cart.getUser().getAddress()
        );
        order.setCoupon(coupon);
        order.setTotalAmount(totalAmount);
        order.setCouponDiscount(couponDiscount);
        order.setFinalAmount(finalAmount);

        Order savedOrder = orderRepository.save(order);

        // Navigate to CartItem => OrderDetail
        List<OrderDetail> orderDetails = cartItems.stream()
                .map(item -> {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrder(savedOrder);
                    detail.setProduct(item.getProduct());
                    detail.setQuantity(item.getQuantity());
                    detail.setUnitPrice(item.getProduct().getPrice());
                    detail.setTotalPrice(item.getProduct().getPrice() * item.getQuantity());
                    return detail;
                })
                .collect(Collectors.toList());

        orderDetailRepository.saveAll(orderDetails);

        // [QUÊN MẤT BƯỚC NÀY]: Chưa xóa cart sau khi tạo đơn hàng xong!

        savedOrder.setOrderDetails(new HashSet<>(orderDetails));
        return orderMapper.toResponseDTO(savedOrder);
    }

    // Các hàm khác giữ nguyên
    @Override
    public List<OrderResponseDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUser_IdUser(userId).stream().map(orderMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toResponseDTO).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) throw new RuntimeException("No orders found");
        return orders.stream().map(orderMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(newStatus);
        return orderMapper.toResponseDTO(orderRepository.save(order));
    }
}