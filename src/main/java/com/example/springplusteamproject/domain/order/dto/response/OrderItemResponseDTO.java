package com.example.springplusteamproject.domain.order.dto.response;

import com.example.springplusteamproject.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponseDTO{

    private Long flowerId;
    private String flowerName; //꽃
    private int itemPrice; // 단가 * 수량 (1메뉴 총액)
    private int quantity; // 수량
    private Long itemDiscount; // 할인
    private Long totalPrice; //최종 금액

    public static OrderItemResponseDTO from(OrderItem item){
        return OrderItemResponseDTO.builder()
            .flowerName(item.getFlower().getName())
            .itemPrice(item.getPrice().getPriceTotal())
            .quantity(item.getQuantity())
            .itemDiscount(item.getPrice().getDiscount())
            .totalPrice(item.getPrice().getFinalPrice())
            .build();
    }
}
