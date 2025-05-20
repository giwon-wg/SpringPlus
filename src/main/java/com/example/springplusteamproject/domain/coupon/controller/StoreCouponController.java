package com.example.springplusteamproject.domain.coupon.controller;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.common.status.SuccessStatus;
import com.example.springplusteamproject.domain.coupon.dto.response.AvailableUserCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.dto.response.UserCouponIssueResponseDto;
import com.example.springplusteamproject.domain.coupon.service.UserCouponService;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores/{storeId}/coupons")
public class StoreCouponController {

    private final UserCouponService userCouponService;

    @Operation(
        summary = "발급 가능한 쿠폰 목록 조회",
        description = "발급 가능한 쿠폰 목록을 조회합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<AvailableUserCouponResponseDto>>> findAvailableUserCoupons(
        @PathVariable Long storeId, @AuthenticationPrincipal CustomUserPrincipal principal) {

        List<AvailableUserCouponResponseDto> responseDto = userCouponService.findAvailableUserCoupons(storeId,
            principal);
        return ApiResponse.onSuccess(SuccessStatus.USER_COUPON_FIND_SUCCESS, responseDto);
    }

    @Operation(
        summary = "발급 가능한 쿠폰 목록 상세 조회",
        description = "발급 가능한 쿠폰을 상세 조회합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<AvailableUserCouponResponseDto>> findAvailableUserCoupon(
        @PathVariable Long storeId, @PathVariable Long couponId,
        @AuthenticationPrincipal CustomUserPrincipal principal) {

        AvailableUserCouponResponseDto responseDto = userCouponService.findAvailableUserCoupon(storeId, couponId,
            principal);
        return ApiResponse.onSuccess(SuccessStatus.USER_COUPON_FIND_SUCCESS, responseDto);
    }

    @Operation(
        summary = "쿠폰 발급",
        description = "쿠폰을 발급받습니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PostMapping("/{couponId}")
    public ResponseEntity<ApiResponse<UserCouponIssueResponseDto>> issueUserCoupon(@PathVariable Long storeId,
                                                                                   @PathVariable Long couponId,
                                                                                   @AuthenticationPrincipal CustomUserPrincipal principal) {

        UserCouponIssueResponseDto responseDto = userCouponService.issueUserCoupon(storeId, couponId, principal);
        return ApiResponse.onSuccess(SuccessStatus.USER_COUPON_ISSUE_SUCCESS, responseDto);
    }
}
