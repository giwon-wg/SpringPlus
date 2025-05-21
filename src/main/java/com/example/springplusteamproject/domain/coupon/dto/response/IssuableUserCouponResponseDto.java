package com.example.springplusteamproject.domain.coupon.dto.response;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IssuableUserCouponResponseDto {

    private final Long id;
    private final String couponName;
    private final Long discount;
    private final LocalDate issuedAt;
    private final LocalDate expiresAt;
    private final Long stock;

    public static IssuableUserCouponResponseDto from(DiscountCoupon discountCoupon) {

        return new IssuableUserCouponResponseDto(discountCoupon.getId(), discountCoupon.getCouponName(),
            discountCoupon.getDiscount(), discountCoupon.getIssuedAt(), discountCoupon.getExpiresAt(),
            discountCoupon.getStock());
    }
}
