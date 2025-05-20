package com.example.springplusteamproject.domain.coupon.controller;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.common.status.SuccessStatus;
import com.example.springplusteamproject.domain.coupon.dto.response.UserCouponIssueResponseDto;
import com.example.springplusteamproject.domain.coupon.service.UserCouponService;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserCouponIssueResponseDto>>> findMyUserCoupons(
        @AuthenticationPrincipal CustomUserPrincipal principal) {

        List<UserCouponIssueResponseDto> responseDto = userCouponService.findMyUserCoupons(principal);
        return ApiResponse.onSuccess(SuccessStatus.USER_COUPON_FIND_SUCCESS, responseDto);
    }
}
