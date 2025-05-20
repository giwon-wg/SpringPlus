package com.example.springplusteamproject.domain.coupon.service;

import com.example.springplusteamproject.domain.coupon.dto.response.AvailableUserCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.dto.response.UserCouponIssueResponseDto;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.util.List;

public interface UserCouponService {

    List<AvailableUserCouponResponseDto> findAvailableUserCoupons(Long storeId, CustomUserPrincipal principal);

    AvailableUserCouponResponseDto findAvailableUserCoupon(Long storeId, Long couponId, CustomUserPrincipal principal);

    UserCouponIssueResponseDto issueUserCoupon(Long storeId, Long couponId, CustomUserPrincipal principal);

    List<UserCouponIssueResponseDto> findMyUserCoupons(CustomUserPrincipal principal);
}
