package com.example.springplusteamproject.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class OrderRequestDTO {

    @Schema(description = "주문할 꽃들",required = true)
    @NotNull(message = "주문 아이템 목록은 필수입니다.")
    private final List<OrderItemRequestDTO> items;

    @Schema(description = "사용할 쿠폰ID", example ="1")
    private final Long userCouponId;

}
