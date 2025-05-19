package com.example.springplusteamproject.domain.coupon.service;

import com.example.springplusteamproject.domain.coupon.dto.request.DiscountCouponRequestDto;
import com.example.springplusteamproject.domain.coupon.dto.response.DiscountCouponResponseDto;
import com.example.springplusteamproject.security.CustomUserPrincipal;

public interface DiscountCouponService {

    DiscountCouponResponseDto createCoupon(Long storeId, DiscountCouponRequestDto requestDto,
                                           CustomUserPrincipal principal);
}
