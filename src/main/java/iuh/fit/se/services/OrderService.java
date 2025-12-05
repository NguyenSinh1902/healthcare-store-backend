package iuh.fit.se.services;

import iuh.fit.se.dtos.order.OrderRequestDTO;
import iuh.fit.se.dtos.order.OrderResponseDTO;
import iuh.fit.se.entities.order.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrderFromCart(Long userId, OrderRequestDTO orderRequestDTO);
    List<OrderResponseDTO> getOrdersByUser(Long userId);
    OrderResponseDTO getOrderById(Long id);

    //get All Orders
    List<OrderResponseDTO> getAllOrders();
    //Update status
    OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus);

}
