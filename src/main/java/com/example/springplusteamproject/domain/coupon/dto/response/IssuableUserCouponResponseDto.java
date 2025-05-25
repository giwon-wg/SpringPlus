package com.example.springplusteamproject.domain.coupon.dto.response;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import java.time.LocalDate;

public record IssuableUserCouponResponseDto(
    Long id,
    String couponName,
    Long discount,
    LocalDate issuedAt,
    LocalDate expiresAt,
    Long stock
) {
    public static IssuableUserCouponResponseDto from(DiscountCoupon coupon) {
        return new IssuableUserCouponResponseDto(
            coupon.getId(),
            coupon.getCouponName(),
            coupon.getDiscount(),
            coupon.getIssuedAt(),
            coupon.getExpiresAt(),
            coupon.getStock()
        );
    }
}
