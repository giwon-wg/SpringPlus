package com.example.springplusteamproject.domain.coupon.controller;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.domain.coupon.dto.request.DiscountCouponRequestDto;
import com.example.springplusteamproject.domain.coupon.dto.response.DiscountCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.service.DiscountCouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DiscountCouponController {

    private final DiscountCouponService discountCouponService;

    @PostMapping("/owner/stores/{storeId}/coupons")
    public ResponseEntity<ApiResponse<DiscountCouponResponseDto>> createCoupon(@PathVariable Long storeId,
                                                                               @Valid @RequestBody DiscountCouponRequestDto requestDto,
                                                                               @AuthenticationPrincipal CustomUserPrincipal principal) {

        DiscountCouponResponseDto responseDto = discountCouponService.createCoupon(storeId, requestDto, principal);
        return ApiResponse.onSuccess(DiscountCouponSuccessCode.DISCOUNT_COUPON_CREATE_SUCCESS, responseDto);
    }
}
