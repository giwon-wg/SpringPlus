package com.example.springplusteamproject.domain.coupon.dto.response;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiscountCouponResponseDto {

    private final Long id;
    private final String couponName;
    private final Long discount;

    public static DiscountCouponResponseDto from(DiscountCoupon discountCoupon) {

        return new DiscountCouponResponseDto(discountCoupon.getId(), discountCoupon.getCouponName(),
            discountCoupon.getDiscount());
    }
}
