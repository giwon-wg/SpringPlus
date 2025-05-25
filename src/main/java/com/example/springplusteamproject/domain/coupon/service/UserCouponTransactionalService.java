package com.example.springplusteamproject.domain.coupon.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.dto.response.UserCouponIssueResponseDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.coupon.repository.UserCouponRepository;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCouponTransactionalService {

    private final StoreRepository storeRepository;

    private final UserCouponRepository userCouponRepository;

    private final DiscountCouponRepository discountCouponRepository;

    @Transactional
    public UserCouponIssueResponseDto issueUserCoupon(Long storeId, Long couponId, User user) {

        validateCouponNotIssued(user.getId(), couponId);

        validateActivateStore(storeId);
        DiscountCoupon issuableCoupon = validateIssuableCoupon(storeId, couponId);

        UserCoupon issueCoupon = createUserCoupon(user, issuableCoupon);
        UserCoupon savedCoupon = userCouponRepository.save(issueCoupon);
        issuableCoupon.decreaseStock();

        return UserCouponIssueResponseDto.from(issuableCoupon, savedCoupon);
    }

    private void validateCouponNotIssued(Long userId, Long couponId) {

        boolean alreadyIssued = userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(userId, couponId);
        if (alreadyIssued) {
            throw new ApiException(ErrorStatus.COUPON_ALREADY_ISSUED);
        }
    }

    private void validateActivateStore(Long storeId) {

        storeRepository.findByIdAndDeletedFalse(storeId).orElseThrow(() -> new ApiException(ErrorStatus.STORE_NOT_FOUND));
    }

    private DiscountCoupon validateIssuableCoupon(Long storeId, Long couponId) {

        DiscountCoupon issuableCoupon = discountCouponRepository.findIssuableCoupon(storeId, couponId)
            .orElseThrow(() -> new ApiException(ErrorStatus.COUPON_NOT_FOUND));

        return issuableCoupon;
    }

    private UserCoupon createUserCoupon(User user, DiscountCoupon discountCoupon) {
        return UserCoupon.builder()
            .user(user)
            .discountCoupon(discountCoupon)
            .isUsed(false)
            .build();
    }
}
