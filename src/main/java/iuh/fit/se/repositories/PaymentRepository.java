package iuh.fit.se.repositories;

import iuh.fit.se.entities.payment.Payment;
import iuh.fit.se.entities.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder_IdOrder(Long orderId);

    //ADMIN

    //Tính tổng doanh thu thực tế (chỉ tính SUCCESS)
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS'")
    Double sumTotalRevenue();

    //Đếm số lượng giao dịch theo trạng thái (SUCCESS, FAILED)
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") PaymentStatus status);

    //Lấy doanh thu theo ngày (7 ngày gần nhất)
    @Query(value = "SELECT DATE(payment_date) as dateStr, SUM(amount) as total " +
            "FROM payments " +
            "WHERE status = 'SUCCESS' AND payment_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(payment_date) " +
            "ORDER BY dateStr ASC", nativeQuery = true)
    List<Object[]> getRevenueLast7Days();

    @Query("SELECT p FROM Payment p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR CONCAT(p.order.idOrder, '') LIKE %:keyword% OR p.transactionRef LIKE %:keyword%) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:startDate IS NULL OR p.paymentDate >= :startDate) AND " +
            "(:endDate IS NULL OR p.paymentDate <= :endDate) " +
            "ORDER BY p.paymentDate DESC")
    List<Payment> findAllPayments(
            @Param("keyword") String keyword,
            @Param("status") PaymentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}