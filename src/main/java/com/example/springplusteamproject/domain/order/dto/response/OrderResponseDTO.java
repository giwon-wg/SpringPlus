package com.example.springplusteamproject.domain.order.dto.response;

import com.example.springplusteamproject.domain.order.entity.Order;
import com.example.springplusteamproject.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponseDTO {
    private Long id; //주문 Id
    OrderStatus orderStatus;
    private int totalPrice;
    private int discount;
    LocalDateTime orderedAt;
    private List<OrderItemResponseDTO> items;

    public static OrderResponseDTO from(Order order) {
        return OrderResponseDTO.builder()
            .id(order.getId())
            .orderStatus(order.getOrderStatus())
            .totalPrice(order.getPrice().getFinalPrice())
            .discount(order.getPrice().getDiscount())
            .orderedAt(order.getCreatedAt())
            .items(
                order.getOrderItems().stream()
                    .map(OrderItemResponseDTO::from)
                    .toList()
            )
            .build();
    }
}

