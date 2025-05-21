package com.example.springplusteamproject.domain.order.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Price {
    private int priceTotal; //상품 총 금액 (수량 * 단가)
    private Long discount; //할인가 (쿠폰적용)
    private Long finalPrice; // 최종 결제 금액

    public static Price of(int priceTotal, Long discount){
        Long finalPrice = priceTotal - discount;
        return new Price(priceTotal,discount,finalPrice);
    }

    private Price(int priceTotal, Long discount, Long finalPrice){
        this.priceTotal = priceTotal;
        this.discount = discount;
        this.finalPrice = finalPrice;
    }

}
