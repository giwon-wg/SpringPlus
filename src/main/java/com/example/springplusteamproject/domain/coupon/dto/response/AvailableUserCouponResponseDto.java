package com.example.springplusteamproject.domain.coupon.dto.response;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailableUserCouponResponseDto {

    private final Long id;
    private final String couponName;
    private final Long discount;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final Long stock;

    public static AvailableUserCouponResponseDto from(DiscountCoupon discountCoupon) {

        return new AvailableUserCouponResponseDto(discountCoupon.getId(), discountCoupon.getCouponName(),
            discountCoupon.getDiscount(), discountCoupon.getIssuedAt(), discountCoupon.getExpiresAt(),
            discountCoupon.getStock());
    }
}
