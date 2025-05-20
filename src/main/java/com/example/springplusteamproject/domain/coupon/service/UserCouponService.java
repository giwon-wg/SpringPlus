package com.example.springplusteamproject.domain.coupon.service;

import com.example.springplusteamproject.domain.coupon.dto.response.IssuableUserCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.dto.response.UserCouponIssueResponseDto;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.util.List;

public interface UserCouponService {

    List<IssuableUserCouponResponseDto> findIssuableUserCoupons(Long storeId, CustomUserPrincipal principal);

    IssuableUserCouponResponseDto findIssuableUserCoupon(Long storeId, Long couponId, CustomUserPrincipal principal);

    UserCouponIssueResponseDto issueUserCoupon(Long storeId, Long couponId, CustomUserPrincipal principal);

    List<UserCouponIssueResponseDto> findMyUserCoupons(CustomUserPrincipal principal);
}
