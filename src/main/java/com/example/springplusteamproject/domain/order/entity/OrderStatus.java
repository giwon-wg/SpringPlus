package com.example.springplusteamproject.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("주문요청"),
    PAID("결제완료"),
    FAILED("결제실패");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
