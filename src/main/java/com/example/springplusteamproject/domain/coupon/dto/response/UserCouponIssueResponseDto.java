package com.example.springplusteamproject.domain.coupon.dto.response;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCouponIssueResponseDto {

    private final Long id;
    private final String couponName;
    private final Long discount;
    private final LocalDate issuedAt;
    private final LocalDate expiresAt;

    public static UserCouponIssueResponseDto from(DiscountCoupon discountCoupon, UserCoupon userCoupon) {

        return new UserCouponIssueResponseDto(userCoupon.getId(), discountCoupon.getCouponName(),
            discountCoupon.getDiscount(), discountCoupon.getIssuedAt(), discountCoupon.getExpiresAt());
    }
}
