package iuh.fit.se.mappers;

import iuh.fit.se.dtos.payment.PaymentResponseDTO;
import iuh.fit.se.entities.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "orderId", source = "order.idOrder")
    @Mapping(target = "status", source = "status")
    PaymentResponseDTO toResponseDTO(Payment payment);
}